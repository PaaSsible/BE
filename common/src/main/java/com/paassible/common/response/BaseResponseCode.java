package com.paassible.common.response;

import org.springframework.http.HttpStatus;

public interface BaseResponseCode {
    HttpStatus getHttpStatus();

    String getCode();

    String getMessage();
}
