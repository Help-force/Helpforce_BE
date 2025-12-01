package com.web.helpforce.user.service;

import com.web.helpforce.config.JwtTokenProvider;
import com.web.helpforce.user.dto.LoginRequestDto;
import com.web.helpforce.user.dto.LoginResponseDto;
import com.web.helpforce.user.dto.SignupRequestDto;
import com.web.helpforce.user.dto.SignupResponseDto;
import com.web.helpforce.user.entity.User;
import com.web.helpforce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    // test
    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto) {
        // 1. 이메일로 사용자 찾기
        User user = userRepository.findByEmail(requestDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 3. JWT 토큰 생성
        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());

        // 4. 응답 반환
        return LoginResponseDto.of(
                accessToken,
                refreshToken,
                user.getId(),
                user.getEmail(),
                user.getNickname()
        );
    }

    @Transactional
    public SignupResponseDto signup(SignupRequestDto requestDto) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 2. 닉네임 중복 체크
        if (userRepository.existsByNickname(requestDto.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 3. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 4. User 엔티티 생성
        User user = User.builder()
                .email(requestDto.getEmail())
                .passwordHash(encodedPassword)
                .nickname(requestDto.getNickname())
                .crmGeneration(requestDto.getCrmGeneration())
                .department(requestDto.getDepartment())
                .build();

        // 5. DB 저장
        User savedUser = userRepository.save(user);

        // 6. 응답 반환
        return SignupResponseDto.of(savedUser.getId(), savedUser.getEmail());
    }
}