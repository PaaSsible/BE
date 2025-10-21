package com.paassible.chatservice.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageReadResponse {
    private Long userId;
    private Long oldLastReadMessageId;
    private Long newLastReadMessageId;

    public static MessageReadResponse from(Long userId, Long oldLastReadMessageId, Long newLastReadMessageId) {
        return MessageReadResponse.builder()
                .userId(userId)
                .oldLastReadMessageId(oldLastReadMessageId)
                .newLastReadMessageId(newLastReadMessageId)
                .build();
    }
}