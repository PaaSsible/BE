package com.paassible.chatservice.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MessageReadDetailResponse {
    private Long messageId;
    private int readCount;
    private List<MessageReadUserResponse> readUsers;

    public static MessageReadDetailResponse from(Long messageId, List<MessageReadUserResponse> readUsers) {
        return MessageReadDetailResponse.builder()
                .messageId(messageId)
                .readCount(readUsers.size())
                .readUsers(readUsers)
                .build();
    }
}