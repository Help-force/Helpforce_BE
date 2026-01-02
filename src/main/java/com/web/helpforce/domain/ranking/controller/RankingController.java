package com.web.helpforce.domain.ranking.controller;

import com.web.helpforce.domain.ranking.dto.TopContributorsResponseDto;
import com.web.helpforce.domain.ranking.service.RankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rankings")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    /**
     * GET /api/rankings/top-contributors
     * 좋아요를 많이 받은 상위 5명 조회
     */
    @GetMapping("/top-contributors")
    public ResponseEntity<TopContributorsResponseDto> getTopContributors() {
        TopContributorsResponseDto response = rankingService.getTopContributors();
        return ResponseEntity.ok(response);
    }
}
