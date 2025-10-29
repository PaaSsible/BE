package com.paassible.recruitservice.notification;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NotificationMessageDto {
    private Long userId;
    private String type;
    private String title;
    private String content;
}