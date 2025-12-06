package com.web.helpforce.domain.answer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerUpdateRequestDto {

    @NotBlank(message = "답변 내용을 입력해주세요.")
    private String body;
}
