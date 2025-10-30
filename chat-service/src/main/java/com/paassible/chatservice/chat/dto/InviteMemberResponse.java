package com.paassible.chatservice.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class InviteMemberResponse {
    private Long userId;
    private String nickname;
}
