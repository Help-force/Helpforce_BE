package com.web.helpforce.user.repository;

import com.web.helpforce.question.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // 특정 질문의 답변 조회
    List<Answer> findByQuestion_IdAndIsDeletedFalseOrderByCreatedAtAsc(Long questionId);

    // 사용자별 답변 조회
    Page<Answer> findByUser_IdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 답변 개수 조회
    long countByQuestion_IdAndIsDeletedFalse(Long questionId);
}