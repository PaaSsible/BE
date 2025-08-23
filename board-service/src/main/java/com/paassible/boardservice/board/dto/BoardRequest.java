package com.paassible.boardservice.board.dto;

import lombok.Getter;

@Getter
public class BoardRequest {
    private String name;
    private String activityType;
    private String detailType;
}
