package com.paassible.boardservice.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ChatRoomRequest {
    private List<Long> participantIds;
}
