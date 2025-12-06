package com.web.helpforce.domain.question.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDetailResponse {

    private QuestionDto question;
    private List<AnswerDto> answers;
    private List<AttachmentDto> attachments;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class QuestionDto {
        private Long id;
        private String title;
        private String body;
        private String status;
        private Integer views;
        private Boolean isBookmarked;
        private Long acceptedAnswerId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private UserSummary user;
        private List<Long> tagIds;
        private Long answerCount;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserSummary {
        private Long id;
        private String nickname;
        private String email;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnswerDto {
        private Long id;
        private String body;
        private Long parentAnswerId;
        private Boolean isAccepted;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private UserSummary user;
        private Long likeCount;
        private Boolean isLiked;
        private List<AnswerDto> replies;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttachmentDto {
        private Long id;
        private Long questionId;
        private Long answerId;
        private String fileUrl;
        private String mimeType;
    }
}
