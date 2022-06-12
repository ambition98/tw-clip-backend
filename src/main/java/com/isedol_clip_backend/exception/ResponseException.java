package com.isedol_clip_backend.exception;

import org.springframework.http.HttpStatus;

public class ResponseException extends RuntimeException {
    private HttpStatus httpStatus;

    public ResponseException() {}

    public ResponseException(String message) {
        super(message);
    }

    public ResponseException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
