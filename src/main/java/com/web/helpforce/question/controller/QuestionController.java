package com.web.helpforce.question.controller;

import com.web.helpforce.question.dto.QuestionListPageResponse;
import com.web.helpforce.question.service.QuestionService;
import com.web.helpforce.user.dto.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    public ResponseEntity<SuccessResponse<QuestionListPageResponse>> getQuestions(
            @RequestParam(required = false) List<Long> tagIds,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        // 현재 로그인한 유저 ID (북마크 확인용)
        Long currentUserId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            currentUserId = Long.parseLong(authentication.getName());
        }

        QuestionListPageResponse response = questionService.getQuestions(
                tagIds, sort, page, size, currentUserId);

        return ResponseEntity.ok(SuccessResponse.of(response));
    }
}