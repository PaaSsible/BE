package com.paassible.userservice.notification.entity;

import com.paassible.common.entity.BaseEntity;
import com.paassible.userservice.notification.entity.enums.NoticeType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    private NoticeType type;

    private String title;

    private String content;
}
