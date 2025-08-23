package com.paassible.boardservice.board.exception;


import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;

public class BoardException extends CustomException {
    public BoardException(ErrorCode errorCode) {
        super(errorCode);
    }
}
