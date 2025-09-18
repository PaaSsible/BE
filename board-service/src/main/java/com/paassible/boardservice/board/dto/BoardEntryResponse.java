package com.paassible.boardservice.board.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class BoardEntryResponse {
    private Long boardId;
    private Long positionId;
}
