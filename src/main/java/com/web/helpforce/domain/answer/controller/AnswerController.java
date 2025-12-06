package com.web.helpforce.domain.answer.controller;

import com.web.helpforce.domain.answer.dto.AnswerCreateRequestDto;
import com.web.helpforce.domain.answer.dto.AnswerCreateResponseDto;
import com.web.helpforce.domain.answer.dto.AnswerUpdateRequestDto;
import com.web.helpforce.domain.answer.dto.AnswerUpdateResponseDto;
import com.web.helpforce.domain.answer.service.AnswerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    // 답변 등록 (댓글/대댓글)
    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<AnswerCreateResponseDto> createAnswer(
            @PathVariable Long questionId,
            @Valid @RequestBody AnswerCreateRequestDto requestDto,
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

        AnswerCreateResponseDto response = answerService.createAnswer(questionId, requestDto, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 답변 수정
    @PatchMapping("/answers/{answerId}")
    public ResponseEntity<AnswerUpdateResponseDto> updateAnswer(
            @PathVariable Long answerId,
            @Valid @RequestBody AnswerUpdateRequestDto requestDto,
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

        AnswerUpdateResponseDto response = answerService.updateAnswer(answerId, requestDto, userId);

        return ResponseEntity.ok(response);
    }
}
