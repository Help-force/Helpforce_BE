package com.web.helpforce.domain.tag.controller;

import com.web.helpforce.domain.tag.dto.TagListResponse;
import com.web.helpforce.domain.tag.dto.TagUsageResponseDto;
import com.web.helpforce.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    /**
     * GET /api/tags
     * 모든 태그 조회
     */
    @GetMapping
    public ResponseEntity<TagListResponse> getAllTags() {
        TagListResponse response = tagService.getAllTags();
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/tags/usage-count
     * 태그 사용 횟수 조회
     */
    @GetMapping("/usage-count")
    public ResponseEntity<TagUsageResponseDto> getTagUsageCount() {
        TagUsageResponseDto response = tagService.getTagUsageCount();
        return ResponseEntity.ok(response);
    }
}