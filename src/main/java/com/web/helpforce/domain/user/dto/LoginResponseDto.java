package com.web.helpforce.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    private String message;
    private UserInfoDto user;
    private String accessToken;
    private String refreshToken;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfoDto {
        private Long id;
        private String email;
        private String nickname;
    }

    public static LoginResponseDto of(String accessToken, String refreshToken, Long userId, String email, String nickname) {
        return LoginResponseDto.builder()
                .message("로그인 성공")
                .user(UserInfoDto.builder()
                        .id(userId)
                        .email(email)
                        .nickname(nickname)
                        .build())
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}