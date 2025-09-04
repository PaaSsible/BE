package com.paassible.boardservice.board.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserBoardResponse {
    private Long boardId;
    private String name;
    private String content;
    private String activityType;
    private String detailType;
    private String status;
    private String owner;
}
