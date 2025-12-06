package com.web.helpforce.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogoutResponseDto {
    private String message;

    public static LogoutResponseDto success() {
        return new LogoutResponseDto("로그아웃 성공");
    }
}