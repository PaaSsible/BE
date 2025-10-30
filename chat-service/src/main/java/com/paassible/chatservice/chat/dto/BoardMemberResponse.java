package com.paassible.chatservice.chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BoardMemberResponse {
    private Long userId;
    private String userName;
}
