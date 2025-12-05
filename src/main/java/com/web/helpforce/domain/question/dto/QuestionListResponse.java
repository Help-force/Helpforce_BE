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
public class QuestionListResponse {
    private Long id;
    private String title;
    private String body;
    private String status;
    private Integer views;

    @JsonProperty("is_bookmarked")
    private Boolean isBookmarked;

    @JsonProperty("accepted_answer_id")
    private Long acceptedAnswerId;

    private UserSummary user;

    @JsonProperty("tag_ids")
    private List<Long> tagIds;

    @JsonProperty("answer_count")
    private Long answerCount;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserSummary {
        private Long id;
        private String nickname;
    }
}