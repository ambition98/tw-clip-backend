package com.isedol_clip_backend.exception;

import javax.naming.AuthenticationException;

public class InvalidJwtException extends AuthenticationException {

    public InvalidJwtException(String message) {
        super(message);
    }
    public InvalidJwtException() {}
}
