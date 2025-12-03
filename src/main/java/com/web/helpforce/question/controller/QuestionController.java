package com.web.helpforce.question.controller;

import com.web.helpforce.question.dto.QuestionCreateRequest;
import com.web.helpforce.question.dto.QuestionCreateResponse;
import com.web.helpforce.question.dto.QuestionListPageResponse;
import com.web.helpforce.question.service.QuestionService;
import com.web.helpforce.user.dto.SuccessResponse;
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

        // 현재 로그인한 유저 ID (북마크 확인용)
        Long currentUserId = null;
        if (authentication != null && authentication.isAuthenticated()) {
            currentUserId = Long.parseLong(authentication.getName());
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
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {

        // DTO 생성
        QuestionCreateRequest request = new QuestionCreateRequest(title, body, tagIds, files);

        // Authorization 헤더에서 userId 추출 (임시로 3L 사용)
        // TODO: JWT 토큰에서 userId 파싱하도록 수정 필요
        Long currentUserId = 3L; // 임시로 하드코딩

        QuestionCreateResponse response = questionService.createQuestion(request, currentUserId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}