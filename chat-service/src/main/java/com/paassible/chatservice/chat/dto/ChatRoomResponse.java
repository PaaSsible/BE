package com.paassible.chatservice.chat.dto;

import com.paassible.chatservice.chat.entity.ChatRoom;
import com.paassible.chatservice.chat.entity.RoomType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ChatRoomResponse {
    private Long id;
    private RoomType type;
    private Long boardId;

    public static ChatRoomResponse from(ChatRoom room) {
        return ChatRoomResponse.builder()
                .id(room.getId())
                .type(room.getType())
                .boardId(room.getBoardId())
                .build();
    }
}