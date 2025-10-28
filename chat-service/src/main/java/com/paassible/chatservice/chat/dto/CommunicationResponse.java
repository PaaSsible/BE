package com.paassible.chatservice.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommunicationResponse {
    private long total;
    private long value;
}