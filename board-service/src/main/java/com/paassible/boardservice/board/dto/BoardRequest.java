package com.paassible.boardservice.board.dto;

import com.paassible.boardservice.board.entity.enums.ActivityType;
import com.paassible.boardservice.board.entity.enums.DetailType;
import lombok.Getter;

@Getter
public class BoardRequest {
    private String name;
    private String content;
    private ActivityType activityType;
    private DetailType detailType;
}
