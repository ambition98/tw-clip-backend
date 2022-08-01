package com.isedol_clip_backend.web.controller;

import com.isedol_clip_backend.auth.JwtTokenProvider;
import com.isedol_clip_backend.exception.RequestException;
import com.isedol_clip_backend.util.*;
import com.isedol_clip_backend.util.aop.CheckRunningTime;
import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.model.TwitchClip;
import com.isedol_clip_backend.web.model.TwitchUser;
import com.isedol_clip_backend.web.model.request.ReqClipsDto;
import com.isedol_clip_backend.web.model.request.ReqTwitchUsersDto;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import com.isedol_clip_backend.web.model.response.RespTwitchClipsDto;
import com.isedol_clip_backend.web.model.response.RespUser;
import com.isedol_clip_backend.web.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
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
import java.text.ParseException;
import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/api/twitch/")
@RequiredArgsConstructor
public class TwitchController {

    private final CallTwitchAPI callTwitchAPI;
    private final AccountService accountService;
    private final TwitchStorage twitchStorage;

    @GetMapping("/users")
    public ResponseEntity<CommonResponse> getTwitchUsers(@Valid ReqTwitchUsersDto requestDto) {

        if(!requestDto.isValid()) {
            return MakeResp.make(HttpStatus.BAD_REQUEST
                    , "Parameter \"id\" and \"login\" is limit 100 or greater then 1");
        }

        if(requestDto.getId().length == 1 && twitchStorage.isIsedol(requestDto.getId()[0])) {
            TwitchUser user = twitchStorage.getIsedolInfo(requestDto.getId()[0]);
            return MakeResp.make(HttpStatus.OK, "Success", new TwitchUser[]{user});
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

        TwitchUser[] users = TwitchJsonModelMapper.userMapping(jsonObject);
        log.info(Arrays.toString(users));

        return MakeResp.make(HttpStatus.OK, "Success", users);
    }

    @CheckRunningTime
    @GetMapping("/clips")
    public ResponseEntity<CommonResponse> getTwitchClips(@NonNull final ReqClipsDto requestDto) {
        log.info("RequestDto: " + requestDto);
        JSONObject jsonObject;

        try {
            jsonObject = callTwitchAPI.requestClips(requestDto);
        } catch (IOException | ParseException e) {
            log.error("Http status: {}, Message: {}", HttpStatus.BAD_REQUEST, e.getMessage());
            return MakeResp.make(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (RequestException e) {
            log.warn("Http status: {}, Message: {}", e.getHttpStatus().value(), e.getMessage());
            return MakeResp.make(e.getHttpStatus(), e.getMessage());
        }
        String cursor = getCursor(jsonObject);
        TwitchClip[] clips;
        try {
            clips = TwitchJsonModelMapper.clipMapping(jsonObject);
        } catch (ParseException e) {
            log.error("Http status: {}, Message: {}", HttpStatus.BAD_REQUEST, e.getMessage());
            return MakeResp.make(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        RespTwitchClipsDto clipsDto = new RespTwitchClipsDto();
        clipsDto.setClips(clips);
        clipsDto.setCursor(cursor);

//        log.info("clipsDto: {}", clipsDto);

        return MakeResp.make(HttpStatus.OK, "Success", clipsDto);
    }

    @GetMapping("/search/user")
    public ResponseEntity<CommonResponse> searchUser(@NonNull final String keyword) throws IOException, RequestException {
        JSONObject searchRes = callTwitchAPI.searchUser(keyword);
        JSONArray arr = searchRes.getJSONArray("data");
        long[] id = new long[arr.length()];
        for(int i=0; i<arr.length(); i++) {
            id[i] = Long.parseLong(arr.getJSONObject(i).getString("id"));
        }

        JSONObject usersObj = callTwitchAPI.requestUser(id, null);
        TwitchUser[] users = TwitchJsonModelMapper.userMapping(usersObj);

        return MakeResp.make(HttpStatus.OK, "Success", users);
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

        try {
            jsonObject = callTwitchAPI.requestUser(new long[]{twitchId}, null);
        } catch (IOException e) {
            log.error("Fail to request twitch user");
            log.warn("Http status: {}, Message: {}", HttpStatus.BAD_REQUEST, e.getMessage());
            return MakeResp.make(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (RequestException e) {
            log.warn("Http status: {}, Message: {}", e.getHttpStatus(), e.getMessage());
            return MakeResp.make(e.getHttpStatus(), e.getMessage());
        }

        TwitchUser[] twitchUser = TwitchJsonModelMapper.userMapping(jsonObject);
        RespUser dto = new RespUser(accessToken, twitchUser[0]);
        log.info("tokenDto: {}", dto);

        return MakeResp.make(HttpStatus.OK, "Success", dto);
    }

    private String getCursor(JSONObject jsonObject) {
        try {
            return jsonObject.getJSONObject("pagination").getString("cursor");
        } catch (JSONException e) {
            return null;
        }
    }
}