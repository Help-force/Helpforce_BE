package com.web.helpforce.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupRequestDto {

    private String email;
    private String password;
    private String nickname;

    @JsonProperty("crm_generation")
    private String crmGeneration;
    private String department;

    @JsonProperty("auth_code")
    private String authCode;
}