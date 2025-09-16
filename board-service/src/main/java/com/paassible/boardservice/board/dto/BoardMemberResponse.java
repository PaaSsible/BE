package com.paassible.boardservice.board.dto;

import com.paassible.boardservice.board.entity.BoardMember;
import com.paassible.boardservice.client.UserResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardMemberResponse {
    private Long userId;
    private String userName;
    private String profileImageUrl;
    private String role;

    public static BoardMemberResponse from(Long userId, String userName, String profileImageUrl, BoardMember ub) {
        return BoardMemberResponse.builder()
                .userId(userId)
                .userName(userName)
                .profileImageUrl(profileImageUrl)
                .role(ub.getRole().name())
                .build();
    }
}
