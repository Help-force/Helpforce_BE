package com.web.helpforce.user.repository;

import com.web.helpforce.question.entity.QuestionBookmark;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuestionBookmarkRepository extends JpaRepository<QuestionBookmark, QuestionBookmark.QuestionBookmarkId> {

    Optional<QuestionBookmark> findByQuestion_IdAndUser_Id(Long questionId, Long userId);

    boolean existsByQuestion_IdAndUser_Id(Long questionId, Long userId);

    Page<QuestionBookmark> findByUser_IdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    void deleteByQuestion_IdAndUser_Id(Long questionId, Long userId);
}