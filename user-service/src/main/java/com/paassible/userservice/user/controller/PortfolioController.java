package com.paassible.userservice.user.controller;

import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import com.paassible.userservice.user.dto.PortfolioRequest;
import com.paassible.userservice.user.dto.PortfolioResponse;
import com.paassible.userservice.user.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
@Tag(name = "포트폴리오 API", description = "조회, 업로드, 수정, 삭제")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PostMapping("/portfolios")
    @Operation(summary = "포트폴리오 업로드", description = "포트폴리오를 업로드 한다.")
    public ResponseEntity<ApiResponse<Void>> createPortfolio(
            @AuthenticationPrincipal UserJwtDto user,
            @RequestBody PortfolioRequest request) {

        portfolioService.createPortfolio(user.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CREATED));
    }

    @PutMapping(value = "/portfolios/{portfolioId}")
    @Operation(summary = "포트폴리오 수정", description = "포트폴리오를 수정한다.")
    public ResponseEntity<ApiResponse<Void>> updatePortfolio(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long portfolioId,
            @RequestBody PortfolioRequest request) {

        portfolioService.updatePortfolio(user.getUserId(), portfolioId, request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.MODIFIED));
    }


    @DeleteMapping("/portfolios/{portfolioId}")
    @Operation(summary = "포트폴리오 삭제", description = "포트폴리오를 삭제한다.")
    public ResponseEntity<ApiResponse<Void>> deletePortfolio(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long portfolioId) {
        portfolioService.deletePortfolio(user.getUserId(), portfolioId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.DELETED));
    }

    @GetMapping("/{userId}/portfolios")
    @Operation(summary = "포트폴리오 조회", description = "해당 유저의 포트폴리오를 조회한다.")
    public ResponseEntity<ApiResponse<List<PortfolioResponse>>> getPortfoliosByUser(@PathVariable Long userId) {
        List<PortfolioResponse> response = portfolioService.getPortfoliosByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @GetMapping("/portfolios/{portfolioId}")
    @Operation(summary = "포트폴리오 상세 조회", description = "해당 유저의 포트폴리오를 상세 조회한다.")
    public ResponseEntity<ApiResponse<PortfolioResponse>> getPortfolioDetail(@PathVariable Long portfolioId) {
        PortfolioResponse response = portfolioService.getPortfolioDetail(portfolioId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }
}
