package com.web.helpforce.domain.question.repository;

import com.web.helpforce.domain.question.entity.QuestionTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionTagRepository extends JpaRepository<QuestionTag, Long> {
    
    // 특정 질문의 모든 태그 삭제
    void deleteByQuestion_Id(Long questionId);
    
    // 각 태그별 사용 횟수 조회
    @Query("SELECT qt.tag.id, COUNT(qt) " +
           "FROM QuestionTag qt " +
           "GROUP BY qt.tag.id " +
           "ORDER BY COUNT(qt) DESC")
    List<Object[]> countUsageByTag();
}
