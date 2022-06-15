package com.isedol_clip_backend.exception;

import org.springframework.http.HttpStatus;

public class RequestException extends Exception {
    private HttpStatus httpStatus;

    public RequestException() {}

    public RequestException(String message) {
        super(message);
    }

    public RequestException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
