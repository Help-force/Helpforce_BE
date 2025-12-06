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
public class AnswerUpdateResponseDto {

    private Long id;
    
    private String body;
    
    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;
}
