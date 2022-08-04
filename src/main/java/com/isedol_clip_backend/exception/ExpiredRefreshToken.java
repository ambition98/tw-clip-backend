package com.isedol_clip_backend.exception;

import javax.naming.AuthenticationException;

public class ExpiredRefreshToken extends AuthenticationException {

    public ExpiredRefreshToken(String message) {
        super(message);
    }
    public ExpiredRefreshToken() {}
}
