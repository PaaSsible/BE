package com.paassible.chatservice.chat.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DirectChatRequest {
    private Long userAId;
    private Long userBId;
}
