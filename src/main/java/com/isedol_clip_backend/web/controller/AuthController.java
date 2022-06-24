package com.isedol_clip_backend.web.controller;

import com.isedol_clip_backend.web.model.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthController {

    @GetMapping("/verify")
    public ResponseEntity<CommonResponse> auth() {
        log.info("auth()");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        log.info("principal: {}", (String) authentication.getPrincipal());
        log.info("name: {}", authentication.getName());
        log.info("authorities: {}", String.valueOf(authentication.getAuthorities()));
        log.info("credential: {}", (String) authentication.getCredentials());
        log.info("isAuthenticated: {}", authentication.isAuthenticated());

        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        log.info("remoteAddr: {}", details.getRemoteAddress());
        log.info("sessionId: {}", details.getSessionId());

        return null;
    }
}
