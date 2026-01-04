package com.web.helpforce.domain.question.controller;

import com.web.helpforce.domain.question.dto.*;
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
@RequestMapping("/questions") // 필요하면 "/api/questions"로 통일
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    private Long getOptionalUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) return null;
        try {
            return Long.parseLong(authentication.getName()); // JwtAuthenticationFilter 기준 "userId"
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Long requireUserId(Authentication authentication) {
        Long userId = getOptionalUserId(authentication);
        if (userId == null) throw new UnauthorizedException("로그인이 필요합니다.");
        return userId;
    }

    @GetMapping
    public ResponseEntity<SuccessResponse<QuestionListPageResponse>> getQuestions(
            @RequestParam(name = "tag_ids", required = false) List<Long> tagIds,
            @RequestParam(name = "search_type", required = false) String searchType,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "latest") String sort,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication
    ) {
        Long currentUserId = getOptionalUserId(authentication);

        QuestionListPageResponse response = questionService.getQuestions(
                tagIds, searchType, keyword, sort, page, size, currentUserId
        );

        return ResponseEntity.ok(SuccessResponse.of(response));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QuestionCreateResponse> createQuestion(
            @RequestParam("title") String title,
            @RequestParam("body") String body,
            @RequestParam(name = "tag_ids", required = false) List<Long> tagIds,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            Authentication authentication
    ) {
        Long currentUserId = requireUserId(authentication);

        QuestionCreateRequest request = new QuestionCreateRequest(title, body, tagIds, files);
        QuestionCreateResponse response = questionService.createQuestion(request, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping(value = "/{questionId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<QuestionUpdateResponse> updateQuestion(
            @PathVariable Long questionId,
            @RequestParam("title") String title,
            @RequestParam("body") String body,
            @RequestParam(name = "tag_ids", required = false) List<Long> tagIds,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            Authentication authentication
    ) {
        Long currentUserId = requireUserId(authentication);

        QuestionUpdateResponse response =
                questionService.updateQuestion(questionId, title, body, tagIds, files, currentUserId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{questionId}")
    public ResponseEntity<QuestionDeleteResponse> deleteQuestion(
            @PathVariable Long questionId,
            Authentication authentication
    ) {
        Long currentUserId = requireUserId(authentication);
        return ResponseEntity.ok(questionService.deleteQuestion(questionId, currentUserId));
    }

    @PostMapping("/{questionId}/accept-answer")
    public ResponseEntity<AcceptAnswerResponse> acceptAnswer(
            @PathVariable Long questionId,
            @RequestBody AcceptAnswerRequest request,
            Authentication authentication
    ) {
        Long currentUserId = requireUserId(authentication);
        return ResponseEntity.ok(questionService.acceptAnswer(questionId, request, currentUserId));
    }

    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionDetailResponse> getQuestionDetail(
            @PathVariable Long questionId,
            Authentication authentication
    ) {
        Long currentUserId = getOptionalUserId(authentication);
        return ResponseEntity.ok(questionService.getQuestionDetail(questionId, currentUserId));
    }

    @PostMapping("/{questionId}/views")
    public ResponseEntity<QuestionViewResponseDto> incrementViews(
            @PathVariable Long questionId,
            Authentication authentication
    ) {
        Long currentUserId = requireUserId(authentication);
        return ResponseEntity.ok(questionService.incrementViews(questionId, currentUserId));
    }
}
