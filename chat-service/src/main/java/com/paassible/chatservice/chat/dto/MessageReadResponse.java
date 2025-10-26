package com.paassible.chatservice.chat.dto;

import com.paassible.chatservice.chat.entity.enums.ReadType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageReadResponse {
    private ReadType type;
    private Long userId;
    private Long messageId;

    public static MessageReadResponse from(Long userId, Long messageId, ReadType type) {
        return MessageReadResponse.builder()
                .type(type)
                .userId(userId)
                .messageId(messageId)
                .build();
    }
}