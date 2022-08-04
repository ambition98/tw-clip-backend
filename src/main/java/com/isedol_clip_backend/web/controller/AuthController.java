package com.isedol_clip_backend.web.controller;

import com.isedol_clip_backend.auth.JwtTokenProvider;
import com.isedol_clip_backend.exception.ExpiredRefreshToken;
import com.isedol_clip_backend.exception.InvalidJwtException;
import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.exception.ApiRequestException;
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
    public ResponseEntity<CommonResponse> refreshAccessToken(HttpServletRequest request) throws IOException, ApiRequestException, NoExistedDataException, ExpiredRefreshToken, InvalidJwtException {
        String jwt = (String) request.getAttribute("jwt");
        Long id = JwtTokenProvider.getIdWithoutValidate(jwt);
        log.info("id: {}", id);
        if(id == null) {
            throw new InvalidJwtException();
        }

        AccountEntity entity;
        entity = accountService.getById(id);
        String refreshToken = entity.getRefreshToken();

        try {
            JwtTokenProvider.getTokenClaims(refreshToken);
        } catch (ExpiredJwtException e) {
            throw new ExpiredRefreshToken();
        } catch (Exception e) {
            throw new InvalidJwtException(e.getMessage());
        }

        JSONArray jsonArray = callTwitchAPI.requestUser(new long[]{id}, null);
        TwitchUser user = twitchMapper.mappingUser(jsonArray);

        String accessToken = JwtTokenProvider.generateUserToken(id);
        RespUser dto = new RespUser(accessToken, user);

        return MakeResp.make(HttpStatus.OK, "Success", dto);
    }

    @GetMapping("/auth/logout")
    public ResponseEntity<CommonResponse> logout() throws NoExistedDataException {
        long id = getId();

        AccountEntity entity = accountService.getById(id);
        entity.setRefreshToken(null);
        entity.setTwitchAccessToken(null);
        entity.setTwitchRefreshToken(null);
        accountService.save(entity);

        return MakeResp.make(HttpStatus.OK, "Success");
    }

    private long getId() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(id);
    }
}
