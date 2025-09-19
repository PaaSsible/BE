package com.paassible.boardservice.board.dto;

import com.paassible.boardservice.board.entity.Board;
import com.paassible.boardservice.client.UserResponse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardResponse {
    private Long boardId;
    private String name;
    private String content;
    private String activityType;
    private String detailType;
    private String status;
    private String owner;

    public static BoardResponse from(Board board, UserResponse owner) {
        return BoardResponse.builder()
                .boardId(board.getId())
                .name(board.getName())
                .content(board.getContent())
                .activityType(board.getActivityType().name())
                .detailType(board.getDetailType() != null ? board.getDetailType().name() : null)
                .status(board.getStatus().name())
                .owner(owner.getNickname())
                .build();
    }
}
