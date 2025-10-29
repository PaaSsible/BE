package com.paassible.chatservice.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NotificationMessageDto {
    private String id;
    private Long userId;
    private String type;
    private String title;
    private String content;
}
