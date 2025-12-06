package com.web.helpforce.domain.answer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerLikeResponseDto {
    private Long answerId;
    private Boolean isLiked;  // 좋아요 상태
    private Long likeCount;   // 좋아요 개수
}
