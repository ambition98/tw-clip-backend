package com.isedol_clip_backend.exception;

import javax.naming.AuthenticationException;

public class InvalidTokenException extends AuthenticationException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
