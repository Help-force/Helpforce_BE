package com.web.helpforce.question.service;

import com.web.helpforce.question.dto.QuestionListPageResponse;
import com.web.helpforce.question.dto.QuestionListResponse;
import com.web.helpforce.question.entity.Question;
import com.web.helpforce.question.repository.QuestionRepository;
import com.web.helpforce.user.repository.AnswerRepository;
import com.web.helpforce.user.repository.QuestionBookmarkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuestionBookmarkRepository questionBookmarkRepository;

    public QuestionListPageResponse getQuestions(
            List<Long> tagIds,
            String sort,
            int page,
            int size,
            Long currentUserId) {

        // 페이징 및 정렬 설정
        Sort sortOption = getSortOption(sort);
        Pageable pageable = PageRequest.of(page - 1, size, sortOption);

        // 질문 조회
        Page<Question> questionPage;
        if (tagIds != null && !tagIds.isEmpty()) {
            // 태그 필터링이 있는 경우
            questionPage = questionRepository.findByTagIdsAndIsDeletedFalse(tagIds, pageable);
        } else {
            // 전체 조회
            questionPage = questionRepository.findByIsDeletedFalse(pageable);
        }

        // DTO 변환
        List<QuestionListResponse> questions = questionPage.getContent().stream()
                .map(question -> toQuestionListResponse(question, currentUserId))
                .collect(Collectors.toList());

        // 페이지네이션 정보
        QuestionListPageResponse.Pagination pagination = QuestionListPageResponse.Pagination.builder()
                .currentPage(page)
                .totalPages(questionPage.getTotalPages())
                .totalItems(questionPage.getTotalElements())
                .itemsPerPage(size)
                .hasPrevious(questionPage.hasPrevious())
                .hasNext(questionPage.hasNext())
                .build();

        // 필터 정보
        QuestionListPageResponse.Filters filters = QuestionListPageResponse.Filters.builder()
                .tagIds(tagIds)
                .sort(sort != null ? sort : "latest")
                .build();

        return QuestionListPageResponse.builder()
                .questions(questions)
                .pagination(pagination)
                .filters(filters)
                .build();
    }

    private QuestionListResponse toQuestionListResponse(Question question, Long currentUserId) {
        // 답변 개수
        long answerCount = answerRepository.countByQuestion_IdAndIsDeletedFalse(question.getId());

        // 북마크 여부
        boolean isBookmarked = false;
        if (currentUserId != null) {
            isBookmarked = questionBookmarkRepository.existsByQuestion_IdAndUser_Id(
                    question.getId(), currentUserId);
        }

        // 태그 ID 목록
        List<Long> tagIds = question.getQuestionTags().stream()
                .map(qt -> qt.getTag().getId())
                .collect(Collectors.toList());

        return QuestionListResponse.builder()
                .id(question.getId())
                .title(question.getTitle())
                .body(question.getBody())
                .status(question.getStatus())
                .views(question.getViews())
                .isBookmarked(isBookmarked)
                .acceptedAnswerId(question.getAcceptedAnswerId())
                .user(QuestionListResponse.UserSummary.builder()
                        .id(question.getUser().getId())
                        .nickname(question.getUser().getNickname())
                        .build())
                .tagIds(tagIds)
                .answerCount(answerCount)
                .build();
    }

    private Sort getSortOption(String sort) {
        if (sort == null || sort.equals("latest")) {
            return Sort.by(Sort.Direction.DESC, "createdAt");
        } else if (sort.equals("views")) {
            return Sort.by(Sort.Direction.DESC, "views");
        }
        return Sort.by(Sort.Direction.DESC, "createdAt");
    }
}