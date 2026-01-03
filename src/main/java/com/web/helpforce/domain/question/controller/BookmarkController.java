package com.web.helpforce.domain.question.controller;

import com.web.helpforce.domain.question.dto.BookmarkToggleResponse;
import com.web.helpforce.domain.question.service.BookmarkService;
import com.web.helpforce.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/questions")
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    @PostMapping("/{questionId}/bookmark")
    public ResponseEntity<BookmarkToggleResponse> toggleBookmark(
            @PathVariable Long questionId,
            Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof Long)) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Long currentUserId = (Long) authentication.getPrincipal();

        BookmarkToggleResponse response = bookmarkService.toggleBookmark(questionId, currentUserId);

        return ResponseEntity.ok(response);
    }
}