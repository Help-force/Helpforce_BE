package com.web.helpforce.domain.user.service;

import com.web.helpforce.domain.question.entity.Question;
import com.web.helpforce.domain.question.entity.QuestionTag;
import com.web.helpforce.domain.question.repository.QuestionRepository;
import com.web.helpforce.domain.user.dto.MyQuestionsResponse;
import com.web.helpforce.domain.user.entity.User;
import com.web.helpforce.domain.user.repository.UserRepository;
import com.web.helpforce.global.exception.NotFoundException;
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
public class UserService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;

    public MyQuestionsResponse getMyQuestions(Long userId, int page, int size) {
        // 1. 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 페이징 설정 (최신순 정렬)
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 3. 내가 작성한 질문 조회
        Page<Question> questionPage = questionRepository.findByUser_IdAndIsDeletedFalse(userId, pageable);

        // 4. DTO 변환
        List<MyQuestionsResponse.MyQuestionDto> questionDtos = questionPage.getContent().stream()
                .map(question -> MyQuestionsResponse.MyQuestionDto.builder()
                        .id(question.getId())
                        .title(question.getTitle())
                        .body(question.getBody())
                        .status(question.getStatus())
                        .views(question.getViews())
                        .acceptedAnswerId(question.getAcceptedAnswerId())
                        .createdAt(question.getCreatedAt())
                        .updatedAt(question.getUpdatedAt())
                        .tagIds(question.getQuestionTags().stream()
                                .map(qt -> qt.getTag().getId())
                                .collect(Collectors.toList()))
                        .answerCount(question.getAnswers().size())
                        .build())
                .collect(Collectors.toList());

        // 5. 페이지네이션 정보
        MyQuestionsResponse.PaginationDto pagination = MyQuestionsResponse.PaginationDto.builder()
                .currentPage(page)
                .totalPages(questionPage.getTotalPages())
                .totalItems(questionPage.getTotalElements())
                .itemsPerPage(size)
                .hasPrevious(questionPage.hasPrevious())
                .hasNext(questionPage.hasNext())
                .build();

        return MyQuestionsResponse.builder()
                .questions(questionDtos)
                .pagination(pagination)
                .build();
    }
}