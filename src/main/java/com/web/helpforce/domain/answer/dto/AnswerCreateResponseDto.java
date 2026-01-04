package com.web.helpforce.domain.answer.dto;

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
public class AnswerCreateResponseDto {

    private Long id;

    @JsonProperty("question_id")
    private Long questionId;

    @JsonProperty("user_id")
    private Long userId;
    private String body;

    @JsonProperty("parent_answer_id")
    private Long parentAnswerId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
