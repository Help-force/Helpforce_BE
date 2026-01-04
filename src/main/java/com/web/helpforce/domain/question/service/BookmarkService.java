package com.web.helpforce.domain.question.service;

import com.web.helpforce.domain.question.dto.BookmarkToggleResponse;
import com.web.helpforce.domain.question.entity.Question;
import com.web.helpforce.domain.question.entity.QuestionBookmark;
import com.web.helpforce.domain.question.repository.QuestionBookmarkRepository;
import com.web.helpforce.domain.question.repository.QuestionRepository;
import com.web.helpforce.domain.user.entity.User;
import com.web.helpforce.domain.user.repository.UserRepository;
import com.web.helpforce.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {

    private final QuestionBookmarkRepository bookmarkRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Transactional
    public BookmarkToggleResponse toggleBookmark(Long questionId, Long userId) {
        // 1. 질문 존재 확인
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new NotFoundException("질문을 찾을 수 없습니다."));

        if (Boolean.TRUE.equals(question.getIsDeleted())) {
            throw new NotFoundException("질문을 찾을 수 없습니다.");
        }

        // 2. 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        // 3. 기존 북마크 확인
        Optional<QuestionBookmark> existingBookmark =
                bookmarkRepository.findByQuestion_IdAndUser_Id(questionId, userId);

        boolean isBookmarked;

        if (existingBookmark.isPresent()) {
            // 북마크가 있으면 삭제 (취소)
            bookmarkRepository.delete(existingBookmark.get());
            isBookmarked = false;
        } else {
            // 북마크가 없으면 생성 (등록)
            QuestionBookmark newBookmark = QuestionBookmark.builder()
                    .question(question)
                    .user(user)
                    .build();
            bookmarkRepository.save(newBookmark);
            isBookmarked = true;
        }

        // 4. 응답 반환
        return BookmarkToggleResponse.builder()
                .questionId(questionId)
                .isBookmarked(isBookmarked)
                .build();
    }
}