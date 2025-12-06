package com.web.helpforce.domain.tag.service;

import com.web.helpforce.domain.tag.dto.TagListResponse;
import com.web.helpforce.domain.tag.entity.Tag;
import com.web.helpforce.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;

    public TagListResponse getAllTags() {
        // 모든 태그 조회
        List<Tag> tags = tagRepository.findAll();

        // DTO로 변환
        List<TagListResponse.TagDto> tagDtos = tags.stream()
                .map(tag -> TagListResponse.TagDto.builder()
                        .id(tag.getId())
                        .name(tag.getName())
                        .build())
                .collect(Collectors.toList());

        return TagListResponse.builder()
                .tags(tagDtos)
                .build();
    }
}