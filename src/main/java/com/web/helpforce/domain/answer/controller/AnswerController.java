package com.web.helpforce.domain.answer.controller;

import com.web.helpforce.domain.answer.dto.AnswerCreateRequestDto;
import com.web.helpforce.domain.answer.dto.AnswerCreateResponseDto;
import com.web.helpforce.domain.answer.dto.AnswerUpdateRequestDto;
import com.web.helpforce.domain.answer.dto.AnswerUpdateResponseDto;
import com.web.helpforce.domain.answer.dto.AnswerDeleteResponseDto;
import com.web.helpforce.domain.answer.service.AnswerService;
import com.web.helpforce.global.exception.UnauthorizedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class AnswerController {

    private final AnswerService answerService;

    // 답변 등록 (댓글/대댓글)
    @PostMapping("/questions/{question_id}/answers")
    public ResponseEntity<AnswerCreateResponseDto> createAnswer(
            @PathVariable("question_id") Long questionId,
            @Valid @RequestBody AnswerCreateRequestDto requestDto,
            Authentication authentication) {

        // 현재 로그인한 사용자 ID
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        Long userId = Long.parseLong(authentication.getName());
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
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        Long userId = Long.parseLong(authentication.getName());
        AnswerUpdateResponseDto response = answerService.updateAnswer(answerId, requestDto, userId);

        return ResponseEntity.ok(response);
    }

    // 답변 삭제
    @DeleteMapping("/answers/{answerId}")
    public ResponseEntity<AnswerDeleteResponseDto> deleteAnswer(
            @PathVariable Long answerId,
            Authentication authentication) {

        // 현재 로그인한 사용자 ID
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }

        Long userId = Long.parseLong(authentication.getName());
        AnswerDeleteResponseDto response = answerService.deleteAnswer(answerId, userId);

        return ResponseEntity.ok(response);
    }
}
