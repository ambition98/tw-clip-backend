package com.isedol_clip_backend.web.controller;

import com.isedol_clip_backend.auth.JwtTokenProvider;
import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import com.isedol_clip_backend.web.service.AccountService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AccountService accountService;

    @GetMapping("/auth/verify")
    public ResponseEntity<CommonResponse> verify(HttpServletRequest request) {

        return MakeResp.make(HttpStatus.OK, "USER");
    }

    @GetMapping("/refresh")
    public ResponseEntity<CommonResponse> refreshAccessToken() {
        long id = getId();

        AccountEntity entity;
        try {
            entity = accountService.getById(id);
        } catch (NoExistedDataException e) {
            return MakeResp.make(HttpStatus.INTERNAL_SERVER_ERROR, "Not Existed Account Id");
        }

        String refreshToken = entity.getRefreshToken();

        try {
            JwtTokenProvider.getTokenClaims(refreshToken);
        } catch (ExpiredJwtException e) {
            return MakeResp.make(HttpStatus.BAD_REQUEST, "Expired refresh token. Need Login");
        } catch (Exception e) {
            return MakeResp.make(HttpStatus.UNAUTHORIZED, "Need Login");
        }

        String accessToken = JwtTokenProvider.generateUserToken(id);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("accessToken", accessToken);

        return MakeResp.make(HttpStatus.OK, "Success", jsonObject);
    }

    @GetMapping("/auth/logout")
    public ResponseEntity<CommonResponse> logout() {
        long id = getId();

        try {
            AccountEntity entity = accountService.getById(id);
            entity.setRefreshToken(null);
            entity.setTwitchAccessToken(null);
            entity.setTwitchRefreshToken(null);

            accountService.save(entity);
        } catch (NoExistedDataException e) {
            return MakeResp.make(HttpStatus.INTERNAL_SERVER_ERROR, "Not Existed Account Id");
        }

        return MakeResp.make(HttpStatus.OK, "Success");
    }

    private long getId() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(id);
    }
}
