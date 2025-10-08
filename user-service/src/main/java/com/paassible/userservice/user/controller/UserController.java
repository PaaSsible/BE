package com.paassible.userservice.user.controller;

import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import com.paassible.common.security.jwt.JwtUtil;
import com.paassible.userservice.user.dto.UserResponse;
import com.paassible.userservice.user.entity.User;
import com.paassible.userservice.user.service.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "유저 API", description = "유저 조회, 탈퇴, 역할 변경")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/me")
    @Operation(summary = "유저 정보 조회", description = "로그인 한 유저 정보를 조회한다.")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserJwtDto user) {
        UserResponse response = userService.getUserInfo(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @PostMapping("/withdrawal")
    @Operation(summary = "회원 탈퇴", description = "유저가 회원을 탈퇴한다.")
    public ResponseEntity<ApiResponse<Void>> deleteMyAccount(
            @AuthenticationPrincipal UserJwtDto user) {
        userService.deleteUser(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.WITHDRAWAL));
    }

    @PatchMapping("/terms")
    @Operation(summary = "약관 동의", description = "회원가입 후 약관에 동의한다.")
    public ResponseEntity<ApiResponse<Void>> setRole(@AuthenticationPrincipal UserJwtDto user) {
        userService.agreeTerms(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.AGREE));
    }

    @Hidden
    @GetMapping("/internal/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        UserResponse response = userService.getUserInfo(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test/{userId}")
    @Operation(summary = "유저 엑세스 토큰 발급(테스트용)")
    public ResponseEntity<String> getCurrentUser(@PathVariable Long userId) {
        User user = userService.getUser(userId);
        String accessToken = jwtUtil.createAccessToken(userId, user.getRole(), user.isTermsAgreed());
        return ResponseEntity.ok(accessToken);
    }
}
