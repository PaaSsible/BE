package com.paassible.chatservice.chat.dto;

import com.paassible.chatservice.chat.entity.MessageType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRequest {
    private Long senderId;
    private String content;
    private MessageType type;
}