package com.paassible.chatservice.chat.exception;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;

public class ChatException extends CustomException {
    public ChatException(ErrorCode errorCode) {
        super(errorCode);
    }
}

