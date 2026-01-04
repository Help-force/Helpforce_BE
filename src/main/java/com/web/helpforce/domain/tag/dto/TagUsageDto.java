package com.web.helpforce.domain.tag.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagUsageDto {
    private Long id;
    private String name;

    @JsonProperty("usage_count")
    private Long usageCount;
}
