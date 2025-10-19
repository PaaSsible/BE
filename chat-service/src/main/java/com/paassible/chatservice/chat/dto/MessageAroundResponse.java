package com.paassible.chatservice.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MessageAroundResponse {
    private List<ChatMessageResponse> before;
    private ChatMessageResponse center;
    private List<ChatMessageResponse> after;
}