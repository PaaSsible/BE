package com.paassible.userservice.notification.controller;

import com.paassible.common.response.ApiResponse;
import com.paassible.common.response.SuccessCode;
import com.paassible.common.security.dto.UserJwtDto;
import com.paassible.userservice.notification.dto.NotificationResponse;
import com.paassible.userservice.notification.service.NotificationService;
import com.paassible.userservice.notification.sse.SseEmitterManager;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/users/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SseEmitterManager emitterManager;

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(@AuthenticationPrincipal UserJwtDto user) {
        List<NotificationResponse> response = notificationService.getNotifications(user.getUserId());
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK, response));

    }

    @GetMapping("/stream/{userId}")
    public SseEmitter stream(@PathVariable Long userId) {
        return emitterManager.createEmitter(userId);
    }
}