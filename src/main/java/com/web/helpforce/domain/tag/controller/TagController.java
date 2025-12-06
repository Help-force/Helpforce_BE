package com.web.helpforce.domain.tag.controller;

import com.web.helpforce.domain.tag.dto.TagListResponse;
import com.web.helpforce.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<TagListResponse> getAllTags() {
        TagListResponse response = tagService.getAllTags();
        return ResponseEntity.ok(response);
    }
}