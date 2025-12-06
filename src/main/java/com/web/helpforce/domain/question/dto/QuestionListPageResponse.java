package com.web.helpforce.domain.question.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionListPageResponse {
    private List<QuestionListResponse> questions;
    private Pagination pagination;
    private Filters filters;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Pagination {
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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filters {
        @JsonProperty("tag_ids")
        private List<Long> tagIds;

        private String sort;
    }
}