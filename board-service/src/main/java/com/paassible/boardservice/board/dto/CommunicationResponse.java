package com.paassible.boardservice.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommunicationResponse {
    private long total;
    private long value;
}