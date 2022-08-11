package com.isedol_clip_backend.auth;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserAuthentication extends UsernamePasswordAuthenticationToken {

    public UserAuthentication(User principal, String credentials) {
        super(principal, credentials);
    }

    public UserAuthentication(User principal, String credentials,
                              Collection<? extends GrantedAuthority> authorities) {
        super(principal, credentials, authorities);
    }
}