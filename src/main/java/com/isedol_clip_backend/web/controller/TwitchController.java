package com.isedol_clip_backend.web.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isedol_clip_backend.auth.JwtTokenProvider;
import com.isedol_clip_backend.exception.RequestException;
import com.isedol_clip_backend.util.CallTwitchAPI;
import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.util.TwitchJsonModelMapper;
import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.model.TwitchClip;
import com.isedol_clip_backend.web.model.TwitchUser;
import com.isedol_clip_backend.web.model.request.ReqClipRequestDto;
import com.isedol_clip_backend.web.model.request.ReqTwitchUsersDto;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import com.isedol_clip_backend.web.model.response.RespTwitchClipsDto;
import com.isedol_clip_backend.web.model.response.RespTwitchUsersDto;
import com.isedol_clip_backend.web.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/twitch/")
@RequiredArgsConstructor
public class TwitchController {

    private final CallTwitchAPI callTwitchAPI;
    private final AccountService accountService;

    @GetMapping("/users")
    public ResponseEntity<CommonResponse> getTwitchUsers(@Valid ReqTwitchUsersDto requestDto) {

        if(!requestDto.isValid()) {
            return MakeResp.make(HttpStatus.BAD_REQUEST, "parameter \"id\" and \"login\" is limit 100");
        }


        JSONObject jsonObject;

        try {
            jsonObject = callTwitchAPI.requestUser(requestDto.getId(), requestDto.getLogin());
        } catch (IOException e) {
            log.error("Fail to request twitch user");
            log.warn("Http status: {}, Message: {}", HttpStatus.BAD_REQUEST, e.getMessage());
            return MakeResp.make(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (RequestException e) {
            log.warn("Http status: {}, Message: {}", e.getHttpStatus(), e.getMessage());
            return MakeResp.make(e.getHttpStatus(), e.getMessage());
        }

        TwitchUser[] twitchUsers = TwitchJsonModelMapper.userMapping(jsonObject);
        RespTwitchUsersDto twitchUsersDto = new RespTwitchUsersDto();
        twitchUsersDto.setUsers(twitchUsers);

        try {
            String json = new ObjectMapper().writeValueAsString(twitchUsersDto);
        } catch (JsonProcessingException e) {
            return MakeResp.make(HttpStatus.INTERNAL_SERVER_ERROR, "JSON Parsing error");
        }

        log.info(twitchUsersDto.toString());

        return MakeResp.make(HttpStatus.OK, "Success", twitchUsersDto);
    }

    @GetMapping("/clips")
    public ResponseEntity<CommonResponse> getTwitchClips(@NonNull final ReqClipRequestDto requestDto) {
        log.info("RequestDto: " + requestDto);
        JSONObject jsonObject;

        try {
            jsonObject = callTwitchAPI.requestClips(requestDto);
        } catch (IOException e) {
//            log.error("Fail to request twitch user");
            log.error("Http status: {}, Message: {}", HttpStatus.BAD_REQUEST, e.getMessage());
            return MakeResp.make(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (RequestException e) {
            log.warn("Http status: {}, Message: {}", e.getHttpStatus().value(), e.getMessage());
            return MakeResp.make(e.getHttpStatus(), e.getMessage());
        }

        String cursor = jsonObject.getJSONObject("pagination").getString("cursor");
        TwitchClip[] clips = TwitchJsonModelMapper.clipMapping(jsonObject);

        RespTwitchClipsDto clipsDto = new RespTwitchClipsDto();
        clipsDto.setClips(clips);
        clipsDto.setCursor(cursor);

        return MakeResp.make(HttpStatus.OK, "Success", clipsDto);
    }

    @GetMapping("/oauth")
    public ResponseEntity<CommonResponse> getTwitchToken(@NonNull final String code,
                                                         final HttpServletResponse response) {
        log.info("OAuth code: " + code);
        JSONObject jsonObject;
        String twitchRefreshToken;
        String twitchAccessToken;
        long twitchId;

        try {
            jsonObject = callTwitchAPI.requestOauth(code);

            twitchAccessToken = jsonObject.getString("access_token");
            twitchRefreshToken = jsonObject.getString("refresh_token");

            jsonObject = callTwitchAPI.requestUserByToken(twitchAccessToken);
            log.info("Twitch Response: {}", jsonObject);

            twitchId = jsonObject.getJSONArray("data").getJSONObject(0).getLong("id");
            log.info("Twitch Id: " + twitchId);

        } catch (IOException e) {
            log.warn("Http status: {}, Message: {}", HttpStatus.BAD_REQUEST, e.getMessage());
            return MakeResp.make(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (RequestException e) {
            log.warn("Http status: {}, Message: {}", e.getHttpStatus(), e.getMessage());
            return MakeResp.make(e.getHttpStatus(), e.getMessage());
        }

        AccountEntity entity = new AccountEntity();
        entity.setId(twitchId);
        entity.setTwitchAccessToken(twitchAccessToken);
        entity.setTwitchRefreshToken(twitchRefreshToken);

        String accessToken = JwtTokenProvider.generateUserToken(entity.getId());
        String refreshToken = JwtTokenProvider.generateRefreshToken(entity.getId());
        entity.setRefreshToken(refreshToken);

        log.info("Access token: {}", accessToken);
        log.info("Refresh token: {}", refreshToken);

        accountService.save(entity);
        response.setHeader("Authorization", "Bearer " + accessToken);

        return MakeResp.make(HttpStatus.OK, "Success");
    }

    @GetMapping
    public ResponseEntity<CommonResponse> getHotClips() {

        return null;
    }
}