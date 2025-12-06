package com.web.helpforce.domain.tag.service;

import com.web.helpforce.domain.question.repository.QuestionTagRepository;
import com.web.helpforce.domain.tag.dto.TagListResponse;
import com.web.helpforce.domain.tag.dto.TagUsageDto;
import com.web.helpforce.domain.tag.dto.TagUsageResponseDto;
import com.web.helpforce.domain.tag.entity.Tag;
import com.web.helpforce.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TagService {

    private final TagRepository tagRepository;
    private final QuestionTagRepository questionTagRepository;

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

    /**
     * 태그 사용 횟수 조회
     */
    public TagUsageResponseDto getTagUsageCount() {
        System.out.println("=== Tag Usage Count Query Debug ===");

        try {
            // 1. 모든 태그 조회
            List<Tag> allTags = tagRepository.findAll();
            System.out.println("전체 태그 수: " + allTags.size());

            // 2. 태그별 사용 횟수 조회
            List<Object[]> usageCounts = questionTagRepository.countUsageByTag();
            System.out.println("사용된 태그 수: " + usageCounts.size());

            // 3. Map으로 변환 (tagId -> usageCount)
            Map<Long, Long> usageMap = new HashMap<>();
            for (Object[] result : usageCounts) {
                Long tagId = ((Number) result[0]).longValue();
                Long count = ((Number) result[1]).longValue();
                usageMap.put(tagId, count);
                System.out.println("Tag ID " + tagId + ": " + count + "회 사용");
            }

            // 4. 모든 태그에 대해 DTO 생성 (사용 안된 태그는 0)
            List<TagUsageDto> tagUsages = allTags.stream()
                    .map(tag -> TagUsageDto.builder()
                            .id(tag.getId())
                            .name(tag.getName())
                            .usageCount(usageMap.getOrDefault(tag.getId(), 0L))
                            .build())
                    .collect(Collectors.toList());

            System.out.println("✅ Tag Usage Count 조회 완료");

            // 5. 응답 DTO 생성
            return TagUsageResponseDto.builder()
                    .tags(tagUsages)
                    .build();

        } catch (Exception e) {
            System.out.println("❌ Tag Usage Count 조회 중 오류 발생");
            System.out.println("에러 타입: " + e.getClass().getName());
            System.out.println("에러 메시지: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("태그 사용 횟수 조회 중 오류가 발생했습니다.", e);
        }
    }
}