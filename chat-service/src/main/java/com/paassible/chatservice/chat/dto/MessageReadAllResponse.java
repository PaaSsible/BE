package com.paassible.chatservice.chat.dto;

import com.paassible.chatservice.chat.entity.enums.ReadType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MessageReadAllResponse {
    private ReadType type;
    private Long userId;
    private Long oldLastReadMessageId;
    private Long newLastReadMessageId;

    public static MessageReadAllResponse from(Long userId, Long oldLastReadMessageId, Long newLastReadMessageId) {
        return MessageReadAllResponse.builder()
                .type(ReadType.MESSAGE_READ_ALL)
                .userId(userId)
                .oldLastReadMessageId(oldLastReadMessageId)
                .newLastReadMessageId(newLastReadMessageId)
                .build();
    }
}