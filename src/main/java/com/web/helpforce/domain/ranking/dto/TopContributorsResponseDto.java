package com.web.helpforce.domain.ranking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopContributorsResponseDto {
    private List<TopContributorDto> rankings;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;  // 랭킹 갱신 시간
}
