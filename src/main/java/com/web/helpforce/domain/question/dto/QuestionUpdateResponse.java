package com.web.helpforce.domain.question.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionUpdateResponse {
    
    private String title;
    private String body;
    private List<Long> tagIds;
    
    public static QuestionUpdateResponse of(String title, String body, List<Long> tagIds) {
        return QuestionUpdateResponse.builder()
                .title(title)
                .body(body)
                .tagIds(tagIds)
                .build();
    }
}
