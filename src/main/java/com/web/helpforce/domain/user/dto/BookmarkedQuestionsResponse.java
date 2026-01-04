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
public class BookmarkedQuestionsResponse {

    @JsonProperty("questions")
    private List<BookmarkedQuestionDto> bookmarks;

    private PaginationDto pagination;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookmarkedQuestionDto {
        private Long id;
        private String title;
        private String body;
        private String status;
        private Integer views;

        @JsonProperty("accepted_answer_id")
        private Long acceptedAnswerId;

        @JsonProperty("bookmarked_at")
        private LocalDateTime bookmarkedAt;

        private UserSummaryDto user;

        @JsonProperty("tag_ids")
        private List<Long> tagIds;

        @JsonProperty("answer_count")
        private Long answerCount;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummaryDto {
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
