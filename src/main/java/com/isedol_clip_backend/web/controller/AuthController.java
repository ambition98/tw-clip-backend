package com.isedol_clip_backend.web.controller;

import com.isedol_clip_backend.auth.JwtTokenProvider;
import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.exception.RequestException;
import com.isedol_clip_backend.util.CallTwitchAPI;
import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.util.TwitchMapper;
import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.model.TwitchUser;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import com.isedol_clip_backend.web.model.response.RespUser;
import com.isedol_clip_backend.web.service.AccountService;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AccountService accountService;
    private final CallTwitchAPI callTwitchAPI;
    private final TwitchMapper twitchMapper;
    @GetMapping("/auth/verify")
    public ResponseEntity<CommonResponse> verify(HttpServletRequest request) {

        return MakeResp.make(HttpStatus.OK, "USER");
    }

    @GetMapping("/refresh")
    public ResponseEntity<CommonResponse> refreshAccessToken(HttpServletRequest request) throws IOException, RequestException {
        String jwt = (String) request.getAttribute("jwt");
        Long id = JwtTokenProvider.getIdWithoutValidate(jwt);
        log.info("id: {}", id);
        if(id == null) {
            return null;
        }

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

        JSONArray jsonArray = callTwitchAPI.requestUser(new long[]{id}, null);
        TwitchUser user = twitchMapper.mappingUser(jsonArray);

        String accessToken = JwtTokenProvider.generateUserToken(id);
        RespUser dto = new RespUser(accessToken, user);

        return MakeResp.make(HttpStatus.OK, "Success", dto);
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
