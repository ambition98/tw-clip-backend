package com.isedol_clip_backend.exception;

import javax.naming.AuthenticationException;

public class InvalidParameterException extends AuthenticationException {
    public InvalidParameterException(String message) {
        super(message);
    }
}
