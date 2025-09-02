package com.paassible.meetservice.exception;


import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;

public class MeetException extends CustomException {
    public MeetException(ErrorCode errorCode) {
        super(errorCode);
    }
}
