package com.paassible.chatservice.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageReadResponse {
    private Long lastMessageId;
    private Long readCount;

    public static MessageReadResponse from(Long lastMessageId, Long readCount) {
        return MessageReadResponse.builder()
                .lastMessageId(lastMessageId)
                .readCount(readCount)
                .build();
    }
}