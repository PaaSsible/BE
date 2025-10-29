package com.paassible.userservice.notification.dto;

import com.paassible.userservice.notification.entity.Notification;
import com.paassible.userservice.notification.entity.enums.NoticeType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {

    private Long id;
    private Long userId;
    private NoticeType type;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .userId(notification.getUserId())
                .type(notification.getType())
                .title(notification.getTitle())
                .content(notification.getContent())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}