package com.paassible.chatservice.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomIdResponse {
    private Long roomId;
    private String roomName;
}
