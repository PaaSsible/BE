package com.paassible.userservice.auth.exception;


import com.paassible.common.exception.CustomException;
import com.paassible.common.response.ErrorCode;

public class AuthException extends CustomException {
    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
