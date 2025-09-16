package com.paassible.boardservice.shortcut.exception;


import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;

public class ShortcutException extends CustomException {
    public ShortcutException(ErrorCode errorCode) {
        super(errorCode);
    }
}
