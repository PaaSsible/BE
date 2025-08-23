package com.paassible.userservice.user.exception;

import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;

public class UserException extends CustomException {
    public UserException(ErrorCode errorCode) {
        super(errorCode);
    }
}
