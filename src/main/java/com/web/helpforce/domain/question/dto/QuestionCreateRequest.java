package com.web.helpforce.domain.question.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionCreateRequest {

    private String title;
    private String body;
    private List<Long> tagIds;
    private List<MultipartFile> files;
}
