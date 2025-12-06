package com.web.helpforce.domain.question.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookmarkToggleResponse {

    @JsonProperty("question_id")
    private Long questionId;

    @JsonProperty("is_bookmarked")
    private Boolean isBookmarked;
}