package com.paassible.boardservice.board.dto;

import com.paassible.boardservice.board.entity.Board;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class BoardDetailResponse {
    private Long boardId;
    private String name;
    private String content;
    private String activityType;
    private String detailType;

    public static BoardDetailResponse from(Board board) {
        return BoardDetailResponse.builder()
                .boardId(board.getId())
                .name(board.getName())
                .content(board.getContent())
                .activityType(board.getActivityType().name())
                .detailType(board.getDetailType() != null ? board.getDetailType().name() : null)
                .build();
    }
}
