package com.web.helpforce.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyAnsweredQuestionsResponse {

    private List<AnsweredQuestionDto> questions;
    private PaginationDto pagination;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AnsweredQuestionDto {
        private Long id;
        private String title;
        private String body;
        private String status;
        private Integer views;

        @JsonProperty("is_bookmarked")
        private Boolean isBookmarked;

        @JsonProperty("accepted_answer_id")
        private Long acceptedAnswerId;

        @JsonProperty("is_deleted")
        private Boolean isDeleted;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;

        @JsonProperty("updated_at")
        private LocalDateTime updatedAt;

        @JsonProperty("tag_ids")
        private List<Long> tagIds;

        @JsonProperty("answer_count")
        private Integer answerCount;

        @JsonProperty("my_answer")
        private MyAnswerDto myAnswer;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyAnswerDto {
        private Long id;
        private String body;

        @JsonProperty("parent_answer_id")
        private Long parentAnswerId;

        @JsonProperty("parent_answer")
        private ParentAnswerDto parentAnswer;  // 대댓글인 경우에만

        @JsonProperty("is_accepted")
        private Boolean isAccepted;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParentAnswerDto {
        private Long id;
        private String body;
        private UserInfoDto user;

        @JsonProperty("created_at")
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfoDto {
        private Long id;
        private String nickname;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationDto {
        @JsonProperty("current_page")
        private Integer currentPage;

        @JsonProperty("total_pages")
        private Integer totalPages;

        @JsonProperty("total_items")
        private Long totalItems;

        @JsonProperty("items_per_page")
        private Integer itemsPerPage;

        @JsonProperty("has_previous")
        private Boolean hasPrevious;

        @JsonProperty("has_next")
        private Boolean hasNext;
    }
}