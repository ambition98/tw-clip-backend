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
import com.isedol_clip_backend.web.model.response.RespTwitchUsersDto;
import com.isedol_clip_backend.web.model.response.RespUser;
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
import java.text.ParseException;
import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/api/twitch/")
@RequiredArgsConstructor
public class TwitchController {

    private final CallTwitchAPI callTwitchAPI;
    private final AccountService accountService;
    private final HotclipsStorage hotclipsStorage;

    @GetMapping("/test")
    public ResponseEntity<CommonResponse> test() throws IOException, ParseException, RequestException {
        ReqClipsDto dto = new ReqClipsDto();
        dto.setBroadcasterId("707328484");
        dto.setStartedAt("2022-07-19T15:00:00Z");
        dto.setEndedAt("2022-07-27T15:00:00Z");
        dto.setFirst(100);
        dto.setAfter("eyJiIjpudWxsLCJhIjp7IkN1cnNvciI6Ik5UQXoifX0");

        JSONObject json = callTwitchAPI.requestClips(dto);
        System.out.println(json);
        System.out.println(json.getJSONObject("pagination"));

        return null;
    }

    @GetMapping("/users")
    public ResponseEntity<CommonResponse> getTwitchUsers(@Valid ReqTwitchUsersDto requestDto) {

        if(!requestDto.isValid()) {
            return MakeResp.make(HttpStatus.BAD_REQUEST
                    , "Parameter \"id\" and \"login\" is limit 100 or greater then 1");
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

        log.info(twitchUsersDto.toString());

        return MakeResp.make(HttpStatus.OK, "Success", twitchUsersDto);
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

        String cursor = jsonObject.getJSONObject("pagination").getString("cursor");
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

    @CheckRunningTime
    @GetMapping("/hotclips")
    public ResponseEntity<CommonResponse> getHotclips(final String period, final int page) {
        TwitchClip[] clipsDto = null;
        log.info("period: {}, page: {}", period, page);
        switch (period) {
            case "week":
                clipsDto = hotclipsStorage.getHotclips(HotclipPeirod.WEEK, page);
                break;
            case "month":
                clipsDto = hotclipsStorage.getHotclips(HotclipPeirod.MONTH, page);
                break;
            case "quarter":
                clipsDto = hotclipsStorage.getHotclips(HotclipPeirod.QUARTER, page);
        }

        log.info("dto: {}", Arrays.toString(clipsDto));
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
}