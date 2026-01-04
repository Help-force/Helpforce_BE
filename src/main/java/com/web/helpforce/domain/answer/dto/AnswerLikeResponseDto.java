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
public class AnswerLikeResponseDto {
    @JsonProperty("answer_id")
    private Long answerId;

    @JsonProperty("is_liked")
    private Boolean isLiked;  // 좋아요 상태

    @JsonProperty("like_count")
    private Long likeCount;   // 좋아요 개수
}
