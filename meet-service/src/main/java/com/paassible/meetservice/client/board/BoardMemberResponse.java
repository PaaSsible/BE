package com.paassible.meetservice.client.board;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardMemberResponse {
    private Long userId;
    private String userName;
    private String profileImageUrl;


    public static BoardMemberResponse from(Long userId, String userName, String profileImageUrl) {
        return BoardMemberResponse.builder()
                .userId(userId)
                .userName(userName)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
