package com.isedol_clip_backend.web.controller;

import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class AuthController {

    @GetMapping("/verify")
    public ResponseEntity<CommonResponse> auth() {
        log.info("auth()");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        ArrayList<GrantedAuthority> grantList = (ArrayList<GrantedAuthority>) authentication.getAuthorities().stream().collect(Collectors.toList());
        if() {
            log.info("Unauthorized");

            return MakeResp.make(HttpStatus.UNAUTHORIZED, "Need Login");
        } else {
            log.info("Authenticated");
            log.info("Name: {}", authentication.getName());
            log.info("Principal: {}", (String)authentication.getPrincipal());
            log.info("Credential: {}", (String)authentication.getCredentials());
            log.info("isAuthenticated: {}", authentication.isAuthenticated());

            return MakeResp.make(HttpStatus.OK, "Success");
        }
    }
}
