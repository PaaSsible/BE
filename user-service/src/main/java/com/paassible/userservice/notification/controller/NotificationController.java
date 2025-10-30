package com.paassible.userservice.notification.controller;

import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import com.paassible.userservice.notification.dto.NotificationResponse;
import com.paassible.userservice.notification.entity.enums.NoticeType;
import com.paassible.userservice.notification.service.NotificationService;
import com.paassible.userservice.notification.sse.SseEmitterManager;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/users/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SseEmitterManager emitterManager;

    @GetMapping
    @Operation(summary = "알림 조회", description = "유저의 알림을 전체 또는 특정 타입별로 조회한다.")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal UserJwtDto user,
            @RequestParam(required = false) NoticeType type) {

        List<NotificationResponse> responses = notificationService.getNotifications(user.getUserId(), type);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, responses));
    }

    @GetMapping("/stream/{userId}")
    public SseEmitter stream(@PathVariable Long userId) {
        return emitterManager.createEmitter(userId);
    }
}