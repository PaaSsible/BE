package com.paassible.boardservice.portfolio.controller;

import com.paassible.boardservice.portfolio.dto.PortfolioAiRequest;
import com.paassible.boardservice.portfolio.service.PortfolioAiService;
import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AiController {

    private final PortfolioAiService portfolioAiService;

    @PostMapping("/boards/{boardId}/generate")
    public ResponseEntity<ApiResponse<PortfolioAiRequest>> generatePortfolio(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long boardId
    ) {
        PortfolioAiRequest response = portfolioAiService.generatePortfolioByAi(user.getUserId(), boardId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }
}
