package com.paassible.boardservice.task.exception;


import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;

public class TaskException extends CustomException {
    public TaskException(ErrorCode errorCode) {
        super(errorCode);
    }
}
