package com.web.helpforce.domain.question.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcceptAnswerResponse {
    
    private Long questionId;
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
