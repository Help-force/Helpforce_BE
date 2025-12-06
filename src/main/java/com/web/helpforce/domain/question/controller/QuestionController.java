package com.web.helpforce.domain.question.controller;

import com.web.helpforce.domain.question.dto.AcceptAnswerRequest;
import com.web.helpforce.domain.question.dto.AcceptAnswerResponse;
import com.web.helpforce.domain.question.dto.QuestionCreateRequest;
import com.web.helpforce.domain.question.dto.QuestionCreateResponse;
import com.web.helpforce.domain.question.dto.QuestionDeleteResponse;
import com.web.helpforce.domain.question.dto.QuestionListPageResponse;
import com.web.helpforce.domain.question.dto.QuestionUpdateRequest;
import com.web.helpforce.domain.question.dto.QuestionUpdateResponse;
import com.web.helpforce.domain.question.service.QuestionService;
import com.web.helpforce.domain.user.dto.SuccessResponse;
import com.web.helpforce.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

        // 현재 로그인한 유저 ID (북마크 확인용) - 선택적 인증
        Long currentUserId = null;
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof Long) {
            currentUserId = (Long) authentication.getPrincipal();
        }

        QuestionListPageResponse response = questionService.getQuestions(
                tagIds, sort, page, size, currentUserId);

        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QuestionCreateResponse> createQuestion(
            @RequestParam("title") String title,
            @RequestParam("body") String body,
            @RequestParam(value = "tagIds", required = false) List<Long> tagIds,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            Authentication authentication) {

        // JWT에서 userId 추출
        if (authentication == null || !(authentication.getPrincipal() instanceof Long)) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Long currentUserId = (Long) authentication.getPrincipal();

        // DTO 생성
        QuestionCreateRequest request = new QuestionCreateRequest(title, body, tagIds, files);

        QuestionCreateResponse response = questionService.createQuestion(request, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{questionId}")
    public ResponseEntity<QuestionUpdateResponse> updateQuestion(
            @PathVariable Long questionId,
            @RequestBody QuestionUpdateRequest request,
            Authentication authentication) {

        // JWT에서 userId 추출
        if (authentication == null || !(authentication.getPrincipal() instanceof Long)) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Long currentUserId = (Long) authentication.getPrincipal();

        QuestionUpdateResponse response = questionService.updateQuestion(questionId, request, currentUserId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
            @PathVariable Long questionId,
            Authentication authentication) {

        // JWT에서 userId 추출
        if (authentication == null || !(authentication.getPrincipal() instanceof Long)) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Long currentUserId = (Long) authentication.getPrincipal();

        QuestionDeleteResponse response = questionService.deleteQuestion(questionId, currentUserId);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{questionId}/accept-answer")
    public ResponseEntity<AcceptAnswerResponse> acceptAnswer(
            @PathVariable Long questionId,
            @RequestBody AcceptAnswerRequest request,
            Authentication authentication) {

        // JWT에서 userId 추출
        if (authentication == null || !(authentication.getPrincipal() instanceof Long)) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Long currentUserId = (Long) authentication.getPrincipal();

        AcceptAnswerResponse response = questionService.acceptAnswer(questionId, request, currentUserId);

        return ResponseEntity.ok(response);
    }
}