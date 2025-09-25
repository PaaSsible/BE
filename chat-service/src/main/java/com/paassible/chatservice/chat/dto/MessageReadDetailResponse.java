package com.paassible.chatservice.chat.dto;

import com.paassible.chatservice.client.user.UserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MessageReadDetailResponse {
    private Long messageId;
    private List<UserResponse> readUsers;
    private List<UserResponse> unreadUsers;

    public static MessageReadDetailResponse from(Long messageId, List<UserResponse> readUsers, List<UserResponse> unreadUsers) {
        return MessageReadDetailResponse.builder()
                .messageId(messageId)
                .readUsers(readUsers)
                .unreadUsers(unreadUsers)
                .build();
    }
}