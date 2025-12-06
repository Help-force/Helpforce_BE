package com.web.helpforce.domain.answer.repository;

import com.web.helpforce.domain.answer.entity.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // 특정 질문의 답변 조회
    List<Answer> findByQuestion_IdAndIsDeletedFalseOrderByCreatedAtAsc(Long questionId);

    // 부모 답변이 없는 답변만 조회 (댓글만)
    List<Answer> findByQuestion_IdAndParentAnswerIdIsNullAndIsDeletedFalse(Long questionId);

    // 특정 답변의 대댓글 조회
    List<Answer> findByParentAnswerIdAndIsDeletedFalse(Long parentAnswerId);

    // 사용자별 답변 조회
    Page<Answer> findByUser_IdAndIsDeletedFalseOrderByCreatedAtDesc(Long userId, Pageable pageable);

    // 답변 개수 조회
    long countByQuestion_IdAndIsDeletedFalse(Long questionId);
}