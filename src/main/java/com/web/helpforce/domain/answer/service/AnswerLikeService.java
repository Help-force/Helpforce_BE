package com.web.helpforce.domain.answer.service;

import com.web.helpforce.domain.answer.dto.AnswerLikeResponseDto;
import com.web.helpforce.domain.answer.entity.Answer;
import com.web.helpforce.domain.answer.entity.AnswerLike;
import com.web.helpforce.domain.answer.repository.AnswerLikeRepository;
import com.web.helpforce.domain.answer.repository.AnswerRepository;
import com.web.helpforce.domain.user.entity.User;
import com.web.helpforce.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerLikeService {

    private final AnswerLikeRepository answerLikeRepository;
    private final AnswerRepository answerRepository;
    private final UserRepository userRepository;

    /**
     * 답변 좋아요 토글 (등록/취소)
     * - 좋아요가 없으면 등록
     * - 좋아요가 있으면 취소
     */
    @Transactional
    public AnswerLikeResponseDto toggleAnswerLike(Long answerId, Long userId) {
        System.out.println("=== Answer Like Toggle Debug ===");
        System.out.println("answerId: " + answerId);
        System.out.println("userId: " + userId);

        // 1. 답변 존재 확인
        Answer answer = answerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("답변을 찾을 수 없습니다."));

        // 2. 사용자 존재 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 3. 이미 좋아요 했는지 확인
        boolean isLiked = answerLikeRepository.existsByAnswer_IdAndUser_Id(answerId, userId);
        System.out.println("현재 좋아요 상태: " + isLiked);

        if (isLiked) {
            // 좋아요 취소
            AnswerLike.AnswerLikeId likeId = new AnswerLike.AnswerLikeId(answerId, userId);
            answerLikeRepository.deleteById(likeId);
            System.out.println("✅ 좋아요 취소 완료");
        } else {
            // 좋아요 등록
            AnswerLike answerLike = AnswerLike.builder()
                    .answer(answer)
                    .user(user)
                    .build();
            answerLikeRepository.save(answerLike);
            System.out.println("✅ 좋아요 등록 완료");
        }

        // 4. 좋아요 개수 조회
        long likeCount = answerLikeRepository.countByAnswer_Id(answerId);
        System.out.println("현재 좋아요 개수: " + likeCount);

        // 5. 응답 DTO 생성
        return AnswerLikeResponseDto.builder()
                .answerId(answerId)
                .isLiked(!isLiked)  // 토글 후 상태
                .likeCount(likeCount)
                .build();
    }

    /**
     * 답변 좋아요 개수 조회
     */
    public Long getAnswerLikeCount(Long answerId) {
        // 답변 존재 확인
        if (!answerRepository.existsById(answerId)) {
            throw new IllegalArgumentException("답변을 찾을 수 없습니다.");
        }

        return answerLikeRepository.countByAnswer_Id(answerId);
    }
}
