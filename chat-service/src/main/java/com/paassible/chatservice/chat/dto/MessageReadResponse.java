package com.paassible.chatservice.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageReadResponse {
    private Long roomId;
    private Long userId;
    private Long lastMessageId;

    public static MessageReadResponse from(Long roomId, Long userId, Long lastMessageId) {
        return MessageReadResponse.builder()
                .roomId(roomId)
                .userId(userId)
                .lastMessageId(lastMessageId)
                .build();
    }
}