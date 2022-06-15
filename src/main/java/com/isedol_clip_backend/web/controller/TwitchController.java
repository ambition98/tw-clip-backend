package com.isedol_clip_backend.web.controller;

import com.isedol_clip_backend.auth.JwtTokenProvider;
import com.isedol_clip_backend.exception.RequestException;
import com.isedol_clip_backend.util.CallTwitchAPI;
import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.util.TwitchJsonModelMapper;
import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.model.TwitchClip;
import com.isedol_clip_backend.web.model.TwitchUser;
import com.isedol_clip_backend.web.model.request.ClipRequestDto;
import com.isedol_clip_backend.web.model.response.CommonResponseDto;
import com.isedol_clip_backend.web.model.response.TwitchClipsResponseData;
import com.isedol_clip_backend.web.model.response.TwitchUserResponseData;
import com.isedol_clip_backend.web.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TwitchController {

    private final CallTwitchAPI callTwitchAPI;
    private final AccountService accountService;

    @GetMapping("/test")
    public void test(String[] id) {
        System.out.println(Arrays.toString(id));

    }
    @GetMapping("/user")
    public ResponseEntity<CommonResponseDto> getTwitchUserById(final String[] id, final String[] name) {
        int idLen = id == null ? 0 : id.length;
        int nameLen = name == null ? 0 : name.length;

        if(idLen + nameLen >= 100) {
            //error 추가
            return null;
        }

        JSONObject jsonObject = null;

        try {
            jsonObject = callTwitchAPI.requestUser(id, name);
        } catch (IOException e) {
            log.error("Fail to request twitch user");
//            e.printStackTrace();
//            return MakeResp.make(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown Server error");
            log.warn("Http status: {}, Message: {}", HttpStatus.BAD_REQUEST, e.getMessage());
            return MakeResp.make(HttpStatus.BAD_REQUEST, e.getMessage());

        } catch (RequestException e) {
            log.warn("Http status: {}, Message: {}", e.getHttpStatus(), e.getMessage());
            return MakeResp.make(e.getHttpStatus(), e.getMessage());
        }

        TwitchUser[] twitchUsers = TwitchJsonModelMapper.userMapping(jsonObject);
        TwitchUserResponseData responseData = new TwitchUserResponseData();
        responseData.setUsers(twitchUsers);

        log.info(responseData.toString());

        return MakeResp.make(HttpStatus.OK, "Success", responseData);
    }

//    @GetMapping("/user/name")
//    public ResponseEntity<CommonResponseDto> getTwitchUserByName(@NonNull final String name) {
//        JSONObject jsonObject = null;
//
//        try {
//            jsonObject = callTwitchAPI.requestUserByName(name);
//        } catch (IOException e) {
//            log.error("Fail to request twitch user");
//            e.printStackTrace();
//            return MakeResp.make(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown Server error");
//
//        } catch (RequestException e) {
//            log.warn("Http status: {}, Message: {}", e.getHttpStatus(), e.getMessage());
//            return MakeResp.make(e.getHttpStatus(), e.getMessage());
//        }
//
//        TwitchUser[] twitchUsers = TwitchJsonModelMapper.userMapping(jsonObject);
//        TwitchUserResponseData responseData = new TwitchUserResponseData();
//        responseData.setUsers(twitchUsers);
//
//        log.info(responseData.toString());
//
//        return MakeResp.make(HttpStatus.OK, "Success", responseData);
//    }

    @GetMapping("/clips")
    public ResponseEntity<CommonResponseDto> getTwitchClips(@NonNull final ClipRequestDto requestDto) {
        log.info("RequestDto: " + requestDto.toString());
        JSONObject jsonObject = null;

        try {
            jsonObject = callTwitchAPI.requestClips(requestDto);
        } catch (IOException e) {
            log.error("Fail to request twitch clips");
            e.printStackTrace();
            return MakeResp.make(HttpStatus.INTERNAL_SERVER_ERROR, "Server error");
        } catch (RequestException e) {
            log.error("Http status: {}, Message: {}", e.getHttpStatus().value(), e.getMessage());
            return MakeResp.make(e.getHttpStatus(), e.getMessage());
        }

        JSONArray clipArray = jsonObject.getJSONArray("data");
        String cursor = (String) ((JSONObject)jsonObject.get("pagination")).get("cursor");
        TwitchClip[] clips = new TwitchClip[clipArray.length()];

        for(int i = 0; i <clipArray.length(); i++) {
            TwitchClip clip = new TwitchClip();

            clip.setId(((JSONObject)clipArray.get(i)).getString("id"));
            clip.setTitle(((JSONObject)clipArray.get(i)).getString("title"));
            clip.setCreatedAt(((JSONObject)clipArray.get(i)).getString("created_at"));
            clip.setDuration(((JSONObject)clipArray.get(i)).getDouble("duration"));
            clip.setCreatorName(((JSONObject)clipArray.get(i)).getString("creator_name"));
            clip.setEmbedUrl(((JSONObject)clipArray.get(i)).getString("embed_url"));
            clip.setThumbnailUrl(((JSONObject)clipArray.get(i)).getString("thumbnail_url"));
            clip.setViewCount(((JSONObject)clipArray.get(i)).getInt("view_count"));

            log.info(clip.toString());
            clips[i] = clip;
        }

        TwitchClipsResponseData responseData = new TwitchClipsResponseData();
        responseData.setClips(clips);
        responseData.setCursor(cursor);

        return MakeResp.make(HttpStatus.OK, "Success", responseData);
    }

    @GetMapping("/oauth")
    public ResponseEntity<CommonResponseDto> getTwitchOauth(@NonNull final String code,
                                                            final HttpServletResponse response) {
        log.info("OAuth code: " + code);
        JSONObject jsonObject = null;
        String refreshToken;
        String accessToken;
        long twitchId;
        try {
            jsonObject = callTwitchAPI.requestOauth(code);

            accessToken = jsonObject.getString("access_token");
            refreshToken = jsonObject.getString("refresh_token");

            jsonObject = callTwitchAPI.requestUserByToken(accessToken);
            log.info("Twitch Response: {}", jsonObject);

            twitchId = jsonObject.getJSONArray("data").getJSONObject(0).getLong("id");
            log.info("Twitch Id: " + twitchId);

        } catch (IOException e) {
            log.error("Fail to request twitch clips");
            e.printStackTrace();
            return MakeResp.make(HttpStatus.INTERNAL_SERVER_ERROR, "Server error");
        } catch (RequestException e) {
            throw new RuntimeException(e);
        }

        AccountEntity entity = new AccountEntity();
        entity.setId(twitchId);
        entity.setTwitchAccessToken(accessToken);
        entity.setTwitchRefreshToken(refreshToken);
        accountService.save(entity);

        String token = JwtTokenProvider.generateToken(entity.getId());
        log.info("token: {}", token);
        log.info("id: {}", JwtTokenProvider.getId(token));
        response.setHeader("Authorization", "Bearer " + token);

        return MakeResp.make(HttpStatus.OK, "Success");
    }
}