package com.web.helpforce.domain.question.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDeleteResponse {
    
    private String message;
    private Boolean isDeleted;
    
    public static QuestionDeleteResponse of(String message, Boolean isDeleted) {
        return QuestionDeleteResponse.builder()
                .message(message)
                .isDeleted(isDeleted)
                .build();
    }
}
