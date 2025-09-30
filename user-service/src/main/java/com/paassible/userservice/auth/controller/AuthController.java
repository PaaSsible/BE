package com.paassible.userservice.auth.controller;

import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import com.paassible.userservice.auth.dto.AuthCodeRequest;
import com.paassible.userservice.auth.dto.TokenResponse;
import com.paassible.userservice.auth.oauth.GoogleOAuthService;
import com.paassible.userservice.auth.oauth.GoogleUserInfo;
import com.paassible.userservice.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users/auth")
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "토큰 재발급, 로그아웃")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    @Operation(summary = "토큰 재발급", description = "현재 가진 토큰을 이용해 엑세스 토큰을 재발급합니다.")
    public ResponseEntity<ApiResponse<TokenResponse>> reissueToken(
            HttpServletRequest request, HttpServletResponse response) {
        TokenResponse tokens = authService.refreshAccessToken(request, response);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, tokens));
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "사용자가 로그아웃합니다.")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UserJwtDto user, HttpServletResponse response) {
        authService.logout(user.getUserId(), response);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.LOGOUT));
    }

    @PostMapping("/token")
    @Operation(summary = "토큰 발급", description = "로그인 한 유저에 대해 토큰을 발급합니다.")
    public ResponseEntity<ApiResponse<TokenResponse>> callback(@RequestBody AuthCodeRequest request,
                                                               HttpServletResponse response) {
        TokenResponse token = authService.createAccessToken(request, response);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, token));
    }
}
