package com.web.helpforce.domain.question.repository;

import com.web.helpforce.domain.question.entity.QuestionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QuestionTagRepository extends JpaRepository<QuestionTag, Long> {
    
    // 특정 질문의 모든 태그 삭제
    void deleteByQuestionId(Long questionId);
}
