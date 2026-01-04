package com.web.helpforce.domain.answer.controller;

import com.web.helpforce.domain.answer.dto.AnswerLikeResponseDto;
import com.web.helpforce.domain.answer.service.AnswerLikeService;
import com.web.helpforce.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/answers")
@RequiredArgsConstructor
public class AnswerLikeController {

    private final AnswerLikeService answerLikeService;

    /**
     * POST /api/answers/{answerId}/likes
     * 답변 좋아요 토글 (등록/취소)
     */
    @PostMapping("/{answerId}/likes")
    public ResponseEntity<AnswerLikeResponseDto> toggleAnswerLike(
            @PathVariable Long answerId,
            Authentication authentication) {

        // 현재 로그인한 사용자 ID
        Long userId;
        if (authentication != null && authentication.isAuthenticated()) {
            userId = Long.parseLong(authentication.getName());
        } else {
            throw new UnauthorizedException("로그인이 필요합니다.");  // ✅
        }

        AnswerLikeResponseDto response = answerLikeService.toggleAnswerLike(answerId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/answers/{answerId}/likes
     * 답변 좋아요 개수 조회
     */
    @GetMapping("/{answerId}/likes")
    public ResponseEntity<Map<String, Object>> getAnswerLikeCount(
            @PathVariable Long answerId) {

        Long likeCount = answerLikeService.getAnswerLikeCount(answerId);

        Map<String, Object> response = new HashMap<>();
        response.put("answer_id", answerId);
        response.put("like_count", likeCount);

        return ResponseEntity.ok(response);
    }
}
