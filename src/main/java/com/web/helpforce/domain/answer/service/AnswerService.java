package com.web.helpforce.domain.answer.service;

import com.web.helpforce.domain.answer.dto.AnswerCreateRequestDto;
import com.web.helpforce.domain.answer.dto.AnswerCreateResponseDto;
import com.web.helpforce.domain.answer.dto.AnswerUpdateRequestDto;
import com.web.helpforce.domain.answer.dto.AnswerUpdateResponseDto;
import com.web.helpforce.domain.answer.entity.Answer;
import com.web.helpforce.domain.answer.repository.AnswerRepository;
import com.web.helpforce.domain.question.entity.Question;
import com.web.helpforce.domain.question.repository.QuestionRepository;
import com.web.helpforce.domain.user.entity.User;
import com.web.helpforce.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;

    @Transactional
    public AnswerCreateResponseDto createAnswer(Long questionId, AnswerCreateRequestDto requestDto, Long userId) {
        // 디버깅 로그
        System.out.println("=== Answer Create Debug ===");
        System.out.println("questionId: " + questionId);
        System.out.println("userId: " + userId);
        System.out.println("body: " + requestDto.getBody());
        System.out.println("parentAnswerId: " + requestDto.getParentAnswerId());
        
        // 1. 질문 존재 확인
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new IllegalArgumentException("질문을 찾을 수 없습니다."));

        // 2. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 3. 부모 답변 확인 (대댓글인 경우)
        Answer parentAnswer = null;
        if (requestDto.getParentAnswerId() != null) {
            parentAnswer = answerRepository.findById(requestDto.getParentAnswerId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 답변을 찾을 수 없습니다."));
            
            // 부모 답변이 같은 질문에 속하는지 확인
            if (!parentAnswer.getQuestion().getId().equals(questionId)) {
                throw new IllegalArgumentException("잘못된 부모 답변입니다.");
            }
        }

        // 4. 답변 생성
        Answer answer = Answer.builder()
                .body(requestDto.getBody())
                .question(question)
                .user(user)
                .parentAnswerId(requestDto.getParentAnswerId())
                .isAccepted(false)
                .isDeleted(false)
                .build();

        Answer savedAnswer = answerRepository.save(answer);

        // 5. 응답 DTO 생성
        return AnswerCreateResponseDto.builder()
                .id(savedAnswer.getId())
                .questionId(savedAnswer.getQuestion().getId())
                .userId(savedAnswer.getUser().getId())
                .body(savedAnswer.getBody())
                .parentAnswerId(savedAnswer.getParentAnswerId())
                .createdAt(savedAnswer.getCreatedAt())
                .build();
    }

    @Transactional
    public AnswerUpdateResponseDto updateAnswer(Long answerId, AnswerUpdateRequestDto requestDto, Long userId) {
        // 1. 답변 조회
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("답변을 찾을 수 없습니다."));

        // 2. 삭제된 답변인지 확인
        if (answer.getIsDeleted()) {
            throw new IllegalArgumentException("삭제된 답변입니다.");
        }

        // 3. 작성자 본인 확인
        if (!answer.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("자신의 답변만 수정할 수 있습니다.");
        }

        // 4. 답변 내용 수정
        answer.setBody(requestDto.getBody());

        // 5. 응답 DTO 생성
        return AnswerUpdateResponseDto.builder()
                .id(answer.getId())
                .body(answer.getBody())
                .updatedAt(answer.getUpdatedAt())
                .build();
    }
}
