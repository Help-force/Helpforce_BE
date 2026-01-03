package com.web.helpforce.domain.user.controller;

import com.web.helpforce.domain.user.dto.*;
// import com.web.helpforce.user.dto.*;
import com.web.helpforce.domain.user.service.AuthService;
import com.web.helpforce.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto) {
        LoginResponseDto response = authService.login(requestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponseDto> logout(@RequestHeader("Authorization") String token) {
        return ResponseEntity.ok(LogoutResponseDto.success());
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody SignupRequestDto requestDto) {
        SignupResponseDto response = authService.signup(requestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserInfoResponseDto> getCurrentUser(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof Long)) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Long currentUserId = (Long) authentication.getPrincipal();

        UserInfoResponseDto response = authService.getUserInfo(currentUserId);
        return ResponseEntity.ok(response);
    }
}