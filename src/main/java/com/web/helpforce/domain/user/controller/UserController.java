package com.web.helpforce.domain.user.controller;

import com.web.helpforce.domain.user.dto.BookmarkedQuestionsResponse;
import com.web.helpforce.domain.user.dto.MyAnsweredQuestionsResponse;
import com.web.helpforce.domain.user.dto.MyQuestionsResponse;
import com.web.helpforce.domain.user.service.UserService;
import com.web.helpforce.global.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/questions")
    public ResponseEntity<MyQuestionsResponse> getMyQuestions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof Long)) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Long currentUserId = (Long) authentication.getPrincipal();

        MyQuestionsResponse response = userService.getMyQuestions(currentUserId, page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/answered-questions")
    public ResponseEntity<MyAnsweredQuestionsResponse> getMyAnsweredQuestions(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof Long)) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Long currentUserId = (Long) authentication.getPrincipal();

        MyAnsweredQuestionsResponse response = userService.getMyAnsweredQuestions(currentUserId, page, size);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/bookmarks")
    public ResponseEntity<BookmarkedQuestionsResponse> getMyBookmarks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {

        if (authentication == null || !(authentication.getPrincipal() instanceof Long)) {
            throw new UnauthorizedException("로그인이 필요합니다.");
        }
        Long currentUserId = (Long) authentication.getPrincipal();

        BookmarkedQuestionsResponse response = userService.getBookmarkedQuestions(currentUserId, page, size);

        return ResponseEntity.ok(response);
    }
}