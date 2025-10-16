package com.paassible.chatservice.chat.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ChatRoomRequest {
    private List<Long> participantIds;
}

