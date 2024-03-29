package com.isedol_clip_backend.web.controller;

import com.isedol_clip_backend.auth.JwtTokenProvider;
import com.isedol_clip_backend.exception.ApiRequestException;
import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.util.*;
import com.isedol_clip_backend.util.aop.CheckRunningTime;
import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.model.TwitchClip;
import com.isedol_clip_backend.web.model.TwitchUser;
import com.isedol_clip_backend.web.model.request.ReqClipsDto;
import com.isedol_clip_backend.web.model.request.ReqTwitchUsersDto;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import com.isedol_clip_backend.web.model.response.RespTwitchClipsDto;
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
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/twitch/")
@RequiredArgsConstructor
public class TwitchController {

    private final CallTwitchAPI callTwitchAPI;
    private final AccountService accountService;
    private final TwitchStorage twitchStorage;
    private final TwitchMapper twitchMapper;
    @GetMapping("/users")
    public ResponseEntity<CommonResponse> getTwitchUsers(@Valid ReqTwitchUsersDto requestDto) throws IOException, NoExistedDataException, ApiRequestException {

        if(!requestDto.isValid()) {
            return MakeResp.make(HttpStatus.BAD_REQUEST
                    , "Parameter \"id\" and \"login\" is limit 100 or greater then 1");
        }

        if(requestDto.getId().length == 1 && twitchStorage.isIsedol(requestDto.getId()[0])) {
            TwitchUser user = twitchStorage.getIsedolInfo(requestDto.getId()[0]);
            return MakeResp.make(HttpStatus.OK, "Success", new TwitchUser[]{user});
        }

        JSONArray jsonArray;
        jsonArray = callTwitchAPI.requestUser(requestDto.getId(), requestDto.getLogin());

        TwitchUser[] users = twitchMapper.mappingUsers(jsonArray);
        log.info(Arrays.toString(users));

        return MakeResp.make(HttpStatus.OK, "Success", users);
    }

    @CheckRunningTime
    @GetMapping("/clips")
    public ResponseEntity<CommonResponse> getTwitchClips(@NonNull final ReqClipsDto requestDto) throws IOException, ParseException, NoExistedDataException, ApiRequestException {
        log.info("RequestDto: " + requestDto);
        JSONObject jsonObject;

        jsonObject = callTwitchAPI.requestClips(requestDto);
        String cursor = getCursor(jsonObject);
        List<TwitchClip> clips = twitchMapper.mappingClips(jsonObject);

        RespTwitchClipsDto clipsDto = new RespTwitchClipsDto();
        clipsDto.setClips(clips);
        clipsDto.setCursor(cursor);

        return MakeResp.make(HttpStatus.OK, "Success", clipsDto);
    }

    @GetMapping("/search/user")
    public ResponseEntity<CommonResponse> searchUser(@NonNull final String keyword)
            throws IOException, ApiRequestException, NoExistedDataException {

        JSONObject searchRes = callTwitchAPI.searchUser(keyword);
        JSONArray jsonArray = searchRes.getJSONArray("data");
        long[] id = new long[jsonArray.length()];
        for(int i=0; i<jsonArray.length(); i++) {
            id[i] = Long.parseLong(jsonArray.getJSONObject(i).getString("id"));
        }

        jsonArray = callTwitchAPI.requestUser(id, null);
        TwitchUser[] users = twitchMapper.mappingUsers(jsonArray);

        return MakeResp.make(HttpStatus.OK, "Success", users);
    }
    @GetMapping("/oauth")
    public ResponseEntity<CommonResponse> getTwitchToken(@NonNull final String code,
                                                         final HttpServletResponse response)
            throws IOException, NoExistedDataException, ApiRequestException {

        log.info("OAuth code: " + code);
        JSONObject jsonObject;
        String twitchRefreshToken;
        String twitchAccessToken;

        jsonObject = callTwitchAPI.requestOauth(code);

        twitchAccessToken = jsonObject.getString("access_token");
        twitchRefreshToken = jsonObject.getString("refresh_token");

        jsonObject = callTwitchAPI.requestUserByToken(twitchAccessToken);
        log.info("Twitch Response: {}", jsonObject);

        TwitchUser twitchUser = twitchMapper.mappingUser(jsonObject.getJSONArray("data"));

        AccountEntity entity = new AccountEntity();
        entity.setId(twitchUser.getId());
        entity.setTwitchAccessToken(twitchAccessToken);
        entity.setTwitchRefreshToken(twitchRefreshToken);

        String accessToken = JwtTokenProvider.generateUserToken(entity.getId());
        String refreshToken = JwtTokenProvider.generateRefreshToken(entity.getId());
        entity.setRefreshToken(refreshToken);

        log.info("Access token: {}", accessToken);
        log.info("Refresh token: {}", refreshToken);

        accountService.save(entity);
        CookieUtil.setCookie(response, accessToken);

        return MakeResp.make(HttpStatus.OK, "Success", twitchUser);
    }

    private String getCursor(JSONObject jsonObject) {
        try {
            return jsonObject.getJSONObject("pagination").getString("cursor");
        } catch (JSONException e) {
            return null;
        }
    }
}