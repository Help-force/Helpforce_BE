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
public class QuestionCreateResponse {

    private Long id;
    private LocalDateTime createdAt;
    private List<AttachmentDto> attachments;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AttachmentDto {
        private Long id;
        private String fileUrl;

        public static AttachmentDto of(Long id, String fileUrl) {
            return AttachmentDto.builder()
                    .id(id)
                    .fileUrl(fileUrl)
                    .build();
        }
    }

    public static QuestionCreateResponse of(Long id, LocalDateTime createdAt, List<AttachmentDto> attachments) {
        return QuestionCreateResponse.builder()
                .id(id)
                .createdAt(createdAt)
                .attachments(attachments)
                .build();
    }
}
