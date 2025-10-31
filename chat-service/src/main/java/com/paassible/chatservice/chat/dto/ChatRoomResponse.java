package com.paassible.chatservice.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomResponse {
    private Long roomId;
    private String roomName;
    private String lastMessage;
    @JsonFormat(shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS",
            timezone = "Asia/Seoul")
    private LocalDateTime lastMessageTime;
    private int unreadCount;

    public static ChatRoomResponse from(Long roomId, String roomName, String lastMessage, LocalDateTime lastMessageTime, int unreadCount) {
        return ChatRoomResponse.builder()
                .roomId(roomId)
                .roomName(roomName)
                .lastMessage(lastMessage)
                .lastMessageTime(lastMessageTime)
                .unreadCount(unreadCount)
                .build();
    }
}
