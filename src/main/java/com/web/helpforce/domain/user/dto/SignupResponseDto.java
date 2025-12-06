package com.web.helpforce.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SignupResponseDto {

    private String message;
    private Long userId;
    private String email;

    public static SignupResponseDto of(Long userId, String email) {
        return SignupResponseDto.builder()
                .message("회원가입 성공")
                .userId(userId)
                .email(email)
                .build();
    }
}