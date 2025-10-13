package com.paassible.userservice.user.controller;

import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import com.paassible.userservice.user.dto.PortfolioRequest;
import com.paassible.userservice.user.dto.PortfolioResponse;
import com.paassible.userservice.user.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/portfolios")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "포트폴리도 업로드", description = "포트폴리오를 업로드 한다.")
    public ResponseEntity<ApiResponse<Void>> createPortfolio(
            @AuthenticationPrincipal UserJwtDto user,
            @RequestPart("request") PortfolioRequest request,
            @RequestPart("file") MultipartFile file) {

        portfolioService.createPortfolio(user.getUserId(), request, file);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.CREATED));
    }

    @PutMapping(value = "/{portfolioId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "포트폴리오 수정", description = "포트폴리오를 수정한다.")
    public ResponseEntity<ApiResponse<Void>> updatePortfolio(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long portfolioId,
            @RequestPart("request") PortfolioRequest request,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        portfolioService.updatePortfolio(user.getUserId(), portfolioId, request, file);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.MODIFIED));
    }


    @DeleteMapping("/{portfolioId}")
    @Operation(summary = "포트폴리오 삭제", description = "포트폴리오를 삭제한다.")
    public ResponseEntity<ApiResponse<Void>> deletePortfolio(
            @AuthenticationPrincipal UserJwtDto user,
            @PathVariable Long portfolioId) {
        portfolioService.deletePortfolio(user.getUserId(), portfolioId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.DELETED));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "포트폴리오 조회", description = "해당 유저의 포트폴리오를 조회한다.")
    public ResponseEntity<ApiResponse<List<PortfolioResponse>>> getPortfoliosByUser(@PathVariable Long userId) {
        List<PortfolioResponse> response = portfolioService.getPortfoliosByUser(userId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }
}
