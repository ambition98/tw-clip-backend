package com.isedol_clip_backend.exception;

import org.springframework.http.HttpStatus;

public class ApiRequestException extends Exception {
    private HttpStatus httpStatus;

    public ApiRequestException() {}

    public ApiRequestException(String message) {
        super(message);
    }

    public ApiRequestException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
