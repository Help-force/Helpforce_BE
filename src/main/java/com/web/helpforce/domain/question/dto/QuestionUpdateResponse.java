package com.web.helpforce.domain.question.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionUpdateResponse {

    private String title;
    private String body;

    @JsonProperty("tag_ids")
    private List<Long> tagIds;

    private List<FileDto> files;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class FileDto {
        private Long id;

        @JsonProperty("file_url")
        private String fileUrl;

        @JsonProperty("mime_type")
        private String mimeType;
    }

    public static QuestionUpdateResponse of(String title, String body, List<Long> tagIds, List<FileDto> files) {
        return QuestionUpdateResponse.builder()
                .title(title)
                .body(body)
                .tagIds(tagIds)
                .files(files)
                .build();
    }
}
