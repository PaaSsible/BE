package com.paassible.userservice.notification.service;

import com.paassible.userservice.notification.dto.NotificationResponse;
import com.paassible.userservice.notification.entity.Notification;
import com.paassible.userservice.notification.entity.enums.NoticeType;
import com.paassible.userservice.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponse> getNotifications(Long userId, NoticeType noticeType) {
        List<Notification> notifications;

        if (noticeType == null) {
            notifications = notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
        } else {
            notifications = notificationRepository.findByUserIdAndTypeOrderByCreatedAtDesc(userId, noticeType);
        }

        return notifications.stream()
                .map(NotificationResponse::from)
                .toList();
    }
}
