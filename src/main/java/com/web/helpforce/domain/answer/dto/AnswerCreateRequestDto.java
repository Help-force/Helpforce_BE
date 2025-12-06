package com.web.helpforce.domain.answer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerCreateRequestDto {

    @NotBlank(message = "답변 내용을 입력해주세요.")
    private String body;

    @JsonProperty("parent_answer_id")
    private Long parentAnswerId;  // null이면 댓글, 값 있으면 대댓글
}
