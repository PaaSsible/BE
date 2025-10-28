package com.paassible.boardservice.portfolio.controller;

import com.paassible.boardservice.portfolio.dto.PortfolioAiRequest;
import com.paassible.boardservice.portfolio.service.PortfolioAiService;
import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
@Tag(name = "AI 포트폴리오 API", description = "AI 포트폴리오 생성")
public class AiController {

    private final PortfolioAiService portfolioAiService;

    @PostMapping("/{boardId}/portfolio")
    @Operation(summary = "프로젝트 AI 포트폴리오 생성", description = "해당 프로젝트 보드의 포트폴리오를 AI가 생성한다.")
    public ResponseEntity<ApiResponse<PortfolioAiRequest>> generatePortfolio(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long boardId
    ) {
        PortfolioAiRequest response = portfolioAiService.generatePortfolioByAi(user.getUserId(), boardId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }
}
