package com.web.helpforce.domain.question.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    private Long id;
    private String title;
    private String body;
    private String status;
    private Integer views;

    @JsonProperty("is_bookmarked")
    private Boolean isBookmarked;

    @JsonProperty("accepted_answer_id")
    private Long acceptedAnswerId;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    private UserSummary user;

    @JsonProperty("tag_ids")
    private List<Long> tagIds;

    private List<AttachmentDto> attachments;
    private List<AnswerDto> answers;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserSummary {
        private Long id;
        private String nickname;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttachmentDto {
        private Long id;

        @JsonProperty("file_url")
        private String fileUrl;

        @JsonProperty("mime_type")
        private String mimeType;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnswerDto {
        private Long id;
        private String body;

        @JsonProperty("is_accepted")
        private Boolean isAccepted;

        @JsonProperty("like_count")
        private Long likeCount;

        @JsonProperty("is_liked")
        private Boolean isLiked;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        @JsonProperty("updated_at")
        private LocalDateTime updatedAt;

        private UserSummary user;

        @JsonProperty("child_answers")
        private List<AnswerDto> childAnswers;

        @JsonProperty("parent_answer_id")
        private Long parentAnswerId;
    }
}
