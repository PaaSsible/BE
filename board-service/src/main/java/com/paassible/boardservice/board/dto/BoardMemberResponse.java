package com.paassible.boardservice.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardMemberResponse {
    private Long userId;
    private String userName;
    private String profileImageUrl;
    private String role;
}
