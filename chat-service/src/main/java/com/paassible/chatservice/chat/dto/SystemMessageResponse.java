package com.paassible.chatservice.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.paassible.chatservice.chat.entity.ChatMessage;
import com.paassible.chatservice.chat.entity.enums.MessageType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class SystemMessageResponse {
    private Long id;
    private Long roomId;
    private String content;
    private MessageType type;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS",
            timezone = "Asia/Seoul")
    private LocalDateTime createdAt;

    public static SystemMessageResponse from(ChatMessage message) {
        return SystemMessageResponse.builder()
                .id(message.getId())
                .roomId(message.getRoomId())
                .content(message.getContent())
                .type(MessageType.SYSTEM)
                .createdAt(message.getCreatedAt())
                .build();
    }
}