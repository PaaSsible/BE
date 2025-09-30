package com.paassible.chatservice.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageReadResponse {
    private Long messageId;
    private Long readCount;

    public static MessageReadResponse from(Long messageId, Long readCount) {
        return MessageReadResponse.builder()
                .messageId(messageId)
                .readCount(readCount)
                .build();
    }
}