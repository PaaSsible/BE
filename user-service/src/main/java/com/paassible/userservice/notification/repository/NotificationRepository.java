package com.paassible.userservice.notification.repository;

import com.paassible.userservice.notification.entity.Notification;
import com.paassible.userservice.notification.entity.enums.NoticeType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Notification> findByUserIdAndTypeOrderByCreatedAtDesc(Long userId, NoticeType type);
}