package com.web.helpforce.question.repository;

import com.web.helpforce.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 삭제되지 않은 질문 전체 조회 (페이징)
    Page<Question> findByIsDeletedFalse(Pageable pageable);

    // 태그로 필터링된 질문 조회 (페이징)
    @Query("SELECT DISTINCT q FROM Question q " +
            "JOIN q.questionTags qt " +
            "WHERE qt.tag.id IN :tagIds " +
            "AND q.isDeleted = false")
    Page<Question> findByTagIdsAndIsDeletedFalse(
            @Param("tagIds") List<Long> tagIds,
            Pageable pageable);
}