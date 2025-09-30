package com.paassible.chatservice.chat.dto;

import com.paassible.chatservice.chat.entity.ChatMessage;
import com.paassible.chatservice.chat.entity.enums.MessageType;
import com.paassible.chatservice.client.user.UserResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class ChatMessageResponse {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String content;
    private MessageType type;
    private LocalDateTime createdAt;
    private Long readCount;

    public static ChatMessageResponse from(ChatMessage chatMessage, UserResponse user, Long readCount) {
        return ChatMessageResponse.builder()
                .id(chatMessage.getId())
                .roomId(chatMessage.getRoomId())
                .senderId(user.getId())
                .senderName(user.getNickname())
                .content(chatMessage.getContent())
                .type(chatMessage.getType())
                .createdAt(chatMessage.getCreatedAt())
                .readCount(readCount)
                .build();
    }
}
