package com.web.helpforce.domain.question.repository;

import com.web.helpforce.domain.question.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 삭제되지 않은 질문 전체 조회 (페이징)
    Page<Question> findByIsDeletedFalse(Pageable pageable);

    // 태그로 필터링된 질문 조회 (tagIds 중 하나라도 포함)
    @Query(
            value = """
            SELECT DISTINCT q
            FROM Question q
            JOIN q.questionTags qt
            WHERE q.isDeleted = false
              AND qt.tag.id IN :tagIds
        """,
            countQuery = """
            SELECT COUNT(DISTINCT q.id)
            FROM Question q
            JOIN q.questionTags qt
            WHERE q.isDeleted = false
              AND qt.tag.id IN :tagIds
        """
    )
    Page<Question> findByTagIdsAndIsDeletedFalse(
            @Param("tagIds") List<Long> tagIds,
            Pageable pageable
    );

    // 키워드 검색 (태그 필터 없음)
    @Query(
            value = """
            SELECT q
            FROM Question q
            WHERE q.isDeleted = false
              AND (
                   (:searchType = 'title' AND LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
                OR (:searchType = 'body'  AND LOWER(q.body)  LIKE LOWER(CONCAT('%', :keyword, '%')))
                OR (:searchType = 'all'   AND (
                        LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                     OR LOWER(q.body)  LIKE LOWER(CONCAT('%', :keyword, '%'))
                ))
              )
        """,
            countQuery = """
            SELECT COUNT(q.id)
            FROM Question q
            WHERE q.isDeleted = false
              AND (
                   (:searchType = 'title' AND LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
                OR (:searchType = 'body'  AND LOWER(q.body)  LIKE LOWER(CONCAT('%', :keyword, '%')))
                OR (:searchType = 'all'   AND (
                        LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                     OR LOWER(q.body)  LIKE LOWER(CONCAT('%', :keyword, '%'))
                ))
              )
        """
    )
    Page<Question> searchByKeyword(
            @Param("searchType") String searchType,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // 키워드 검색 + 태그 필터
    @Query(
            value = """
            SELECT DISTINCT q
            FROM Question q
            JOIN q.questionTags qt
            WHERE q.isDeleted = false
              AND qt.tag.id IN :tagIds
              AND (
                   (:searchType = 'title' AND LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
                OR (:searchType = 'body'  AND LOWER(q.body)  LIKE LOWER(CONCAT('%', :keyword, '%')))
                OR (:searchType = 'all'   AND (
                        LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                     OR LOWER(q.body)  LIKE LOWER(CONCAT('%', :keyword, '%'))
                ))
              )
        """,
            countQuery = """
            SELECT COUNT(DISTINCT q.id)
            FROM Question q
            JOIN q.questionTags qt
            WHERE q.isDeleted = false
              AND qt.tag.id IN :tagIds
              AND (
                   (:searchType = 'title' AND LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
                OR (:searchType = 'body'  AND LOWER(q.body)  LIKE LOWER(CONCAT('%', :keyword, '%')))
                OR (:searchType = 'all'   AND (
                        LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
                     OR LOWER(q.body)  LIKE LOWER(CONCAT('%', :keyword, '%'))
                ))
              )
        """
    )
    Page<Question> searchWithTagsAndKeyword(
            @Param("tagIds") List<Long> tagIds,
            @Param("searchType") String searchType,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // my question 조회 (페이징)
    Page<Question> findByUser_IdAndIsDeletedFalse(Long userId, Pageable pageable);
}
