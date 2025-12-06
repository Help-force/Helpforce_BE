package com.web.helpforce.domain.user.service;

import com.web.helpforce.domain.question.entity.Question;
import com.web.helpforce.domain.question.entity.QuestionTag;
import com.web.helpforce.domain.question.repository.QuestionRepository;
import com.web.helpforce.domain.user.dto.MyAnsweredQuestionsResponse;
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
import com.web.helpforce.domain.answer.entity.Answer;
import com.web.helpforce.domain.answer.repository.AnswerRepository;
import com.web.helpforce.domain.question.repository.QuestionBookmarkRepository;
import java.util.LinkedHashMap;
import java.util.Map;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuestionBookmarkRepository questionBookmarkRepository;

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

    public MyAnsweredQuestionsResponse getMyAnsweredQuestions(Long userId, int page, int size) {
        // 1. 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

        // 2. 페이징 설정
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        // 3. 내가 답변한 모든 답변 조회 (댓글 + 대댓글)
        Page<Answer> myAnswers = answerRepository.findByUser_IdAndIsDeletedFalseOrderByCreatedAtAsc(userId, pageable);

        // 4. 질문별로 그룹핑하여 가장 먼저 작성한 답변만 선택
        Map<Long, Answer> questionToFirstAnswer = new LinkedHashMap<>();
        for (Answer answer : myAnswers.getContent()) {
            Long questionId = answer.getQuestion().getId();
            if (!questionToFirstAnswer.containsKey(questionId)) {
                questionToFirstAnswer.put(questionId, answer);
            }
        }

        // 5. DTO 변환
        List<MyAnsweredQuestionsResponse.AnsweredQuestionDto> questionDtos =
                questionToFirstAnswer.values().stream()
                        .map(firstAnswer -> {
                            Question question = firstAnswer.getQuestion();

                            // 내 답변 DTO
                            MyAnsweredQuestionsResponse.MyAnswerDto myAnswerDto =
                                    MyAnsweredQuestionsResponse.MyAnswerDto.builder()
                                            .id(firstAnswer.getId())
                                            .body(firstAnswer.getBody())
                                            .parentAnswerId(firstAnswer.getParentAnswerId())
                                            .isAccepted(firstAnswer.getIsAccepted())
                                            .createdAt(firstAnswer.getCreatedAt())
                                            .build();

                            // 대댓글인 경우 부모 답변 정보 추가
                            if (firstAnswer.getParentAnswerId() != null) {
                                Answer parentAnswer = answerRepository.findById(firstAnswer.getParentAnswerId())
                                        .orElse(null);

                                if (parentAnswer != null) {
                                    MyAnsweredQuestionsResponse.ParentAnswerDto parentDto =
                                            MyAnsweredQuestionsResponse.ParentAnswerDto.builder()
                                                    .id(parentAnswer.getId())
                                                    .body(parentAnswer.getBody())
                                                    .user(MyAnsweredQuestionsResponse.UserInfoDto.builder()
                                                            .id(parentAnswer.getUser().getId())
                                                            .nickname(parentAnswer.getUser().getNickname())
                                                            .build())
                                                    .createdAt(parentAnswer.getCreatedAt())
                                                    .build();

                                    myAnswerDto = MyAnsweredQuestionsResponse.MyAnswerDto.builder()
                                            .id(firstAnswer.getId())
                                            .body(firstAnswer.getBody())
                                            .parentAnswerId(firstAnswer.getParentAnswerId())
                                            .parentAnswer(parentDto)
                                            .isAccepted(firstAnswer.getIsAccepted())
                                            .createdAt(firstAnswer.getCreatedAt())
                                            .build();
                                }
                            }

                            // 북마크 여부 확인
                            boolean isBookmarked = questionBookmarkRepository
                                    .existsByQuestion_IdAndUser_Id(question.getId(), userId);

                            return MyAnsweredQuestionsResponse.AnsweredQuestionDto.builder()
                                    .id(question.getId())
                                    .title(question.getTitle())
                                    .body(question.getBody())
                                    .status(question.getStatus())
                                    .views(question.getViews())
                                    .isBookmarked(isBookmarked)
                                    .acceptedAnswerId(question.getAcceptedAnswerId())
                                    .isDeleted(question.getIsDeleted())
                                    .createdAt(question.getCreatedAt())
                                    .updatedAt(question.getUpdatedAt())
                                    .tagIds(question.getQuestionTags().stream()
                                            .map(qt -> qt.getTag().getId())
                                            .collect(Collectors.toList()))
                                    .answerCount(question.getAnswers().size())
                                    .myAnswer(myAnswerDto)
                                    .build();
                        })
                        .collect(Collectors.toList());

        // 6. 페이지네이션 정보
        MyAnsweredQuestionsResponse.PaginationDto pagination =
                MyAnsweredQuestionsResponse.PaginationDto.builder()
                        .currentPage(page)
                        .totalPages(myAnswers.getTotalPages())
                        .totalItems(myAnswers.getTotalElements())
                        .itemsPerPage(size)
                        .hasPrevious(myAnswers.hasPrevious())
                        .hasNext(myAnswers.hasNext())
                        .build();

        return MyAnsweredQuestionsResponse.builder()
                .questions(questionDtos)
                .pagination(pagination)
                .build();
    }
}