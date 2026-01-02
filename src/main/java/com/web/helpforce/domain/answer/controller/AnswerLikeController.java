package com.web.helpforce.domain.answer.controller;

import com.web.helpforce.domain.answer.dto.AnswerLikeResponseDto;
import com.web.helpforce.domain.answer.service.AnswerLikeService;
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
            // 임시: 인증 없을 때 기본 사용자 ID 3 사용 (테스트용)
            userId = 3L;
            System.out.println("⚠️ Authentication is null, using default userId: 3");
        }

        AnswerLikeResponseDto response = answerLikeService.toggleAnswerLike(answerId, userId);

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/answers/{answerId}/likes
     * 답변 좋아요 개수 조회
     */
    @GetMapping("/{answerId}")
    public ResponseEntity<Map<String, Object>> getAnswerLikeCount(
            @PathVariable Long answerId) {

        Long likeCount = answerLikeService.getAnswerLikeCount(answerId);

        Map<String, Object> response = new HashMap<>();
        response.put("answer_id", answerId);
        response.put("like_count", likeCount);

        return ResponseEntity.ok(response);
    }
}
