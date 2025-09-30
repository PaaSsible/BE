package com.paassible.chatservice.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SubChatRequest {
    private String name;
    private List<Long> memberIds;
}
