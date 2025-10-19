package com.paassible.chatservice.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MessageSearchResponse {
    private int total;
    private List<Long> messageIds;
}