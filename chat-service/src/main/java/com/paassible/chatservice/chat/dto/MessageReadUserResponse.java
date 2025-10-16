package com.paassible.chatservice.chat.dto;

import com.paassible.chatservice.client.user.UserResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageReadUserResponse {
    private String nickname;

    public static MessageReadUserResponse from(UserResponse user) {
        return MessageReadUserResponse.builder()
                .nickname(user.getNickname())
                .build();
    }
}
