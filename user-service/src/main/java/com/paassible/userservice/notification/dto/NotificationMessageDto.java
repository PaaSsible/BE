package com.paassible.userservice.notification.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NotificationMessageDto {
    private String id;
    private Long userId;
    private String type;
    private String title;
    private String content;
}
