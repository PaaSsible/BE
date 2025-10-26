package com.paassible.userservice.user.controller;

import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import com.paassible.common.security.jwt.JwtUtil;
import com.paassible.userservice.user.dto.ProfileRequest;
import com.paassible.userservice.user.dto.UserResponse;
import com.paassible.userservice.user.entity.User;
import com.paassible.userservice.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "유저 API", description = "유저 조회, 탈퇴, 역할 변경")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/profile/{userId}")
    @Operation(summary = "유저 프로필 조회", description = "유저의 프로필을 조회한다.")
    public ResponseEntity<ApiResponse<UserResponse>> getProfile(@PathVariable Long userId) {
        UserResponse response = userService.getProfile(userId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));
    }

    @PutMapping("/profile")
    @Operation(summary = "유저 프로필 수정", description = "기존 프로필 정보를 등록/수정한다.")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            @AuthenticationPrincipal UserJwtDto user,
            @RequestBody ProfileRequest request) {

        userService.updateProfile(user.getUserId(), request);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.MODIFIED));
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

    @GetMapping("/test/{userId}")
    @Operation(summary = "유저 엑세스 토큰 발급(테스트용)")
    public ResponseEntity<String> getCurrentUser(@PathVariable Long userId) {
        User user = userService.getUser(userId);
        String accessToken = jwtUtil.createAccessToken(userId, user.getNickname(), user.getRole(), user.isAgreedToTerms());
        jwtUtil.createRefreshToken(userId);
        return ResponseEntity.ok(accessToken);
    }
}
