package com.web.helpforce.domain.answer.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerDeleteResponseDto {

    private Integer code;
    private String message;

    @JsonProperty("is_deleted")
    private Boolean isDeleted;
}
