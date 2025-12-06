package com.web.helpforce.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDto {
    private Long id;
    private String email;
    private String nickname;

    @JsonProperty("crm_generation")
    private String crmGeneration;

    private String department;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}