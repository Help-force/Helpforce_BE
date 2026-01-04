package com.web.helpforce.domain.question.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcceptAnswerResponse {
    @JsonProperty("question_id")
    private Long questionId;

    @JsonProperty("accepted_answer_id")
    private Long acceptedAnswerId;

    private String message;
    
    public static AcceptAnswerResponse of(Long questionId, Long acceptedAnswerId, String message) {
        return AcceptAnswerResponse.builder()
                .questionId(questionId)
                .acceptedAnswerId(acceptedAnswerId)
                .message(message)
                .build();
    }
}
