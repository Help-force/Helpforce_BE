package com.web.helpforce.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.web.helpforce.domain.user.entity.User;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponseDto {

    @JsonProperty("access_token")
    private String accessToken;

    private UserInfoDto user;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserInfoDto {
        private Long id;
        private String email;
        private String nickname;
    }

    public static LoginResponseDto of(String accessToken, User user) {
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .user(UserInfoDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .nickname(user.getNickname())
                        .build())
                .build();
    }
}
