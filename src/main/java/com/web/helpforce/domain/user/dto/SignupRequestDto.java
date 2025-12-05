package com.web.helpforce.domain.user.dto;

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
    private String crmGeneration;
    private String department;
}