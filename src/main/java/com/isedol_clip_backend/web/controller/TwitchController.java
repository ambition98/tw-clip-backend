package com.isedol_clip_backend.web.controller;

import com.isedol_clip_backend.auth.JwtTokenProvider;
import com.isedol_clip_backend.exception.ResponseException;
import com.isedol_clip_backend.util.CallTwitchAPI;
import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.web.entity.AccountEntity;
import com.isedol_clip_backend.web.model.TwitchClip;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TwitchController {

    private final CallTwitchAPI callTwitchAPI;
    private final AccountService accountService;

    @GetMapping("/user/id/{id}")
    public ResponseEntity<CommonResponseDto> getTwitchUser(@PathVariable @NonNull final String id) {
        JSONObject jsonObject = null;

        try {
            jsonObject = callTwitchAPI.requestUserById(id);
        } catch (IOException e) {
            log.error("Fail to request twitch user");
            e.printStackTrace();
            return MakeResp.make(HttpStatus.INTERNAL_SERVER_ERROR, "Server error");
        } catch (ResponseException e) {
            log.error("Http status: {}, Message: {}", e.getHttpStatus(), e.getMessage());
            return MakeResp.make(e.getHttpStatus(), e.getMessage());
        }

        jsonObject = (JSONObject) ((JSONArray)jsonObject.get("data")).get(0);
        TwitchUserResponseData dto = new TwitchUserResponseData();
        dto.setId((String) jsonObject.get("id"));
        dto.setLogin((String) jsonObject.get("login"));
        dto.setDisplayName((String) jsonObject.get("display_name"));
        dto.setProfileImageUrl((String) jsonObject.get("profile_image_url"));

        log.info(dto.toString());

        return MakeResp.make(HttpStatus.OK, "Success", dto);
    }

    @GetMapping("/user/names")
    public ResponseEntity<CommonResponseDto> getTwitchUsersIdByNames(@NonNull final String[] names) {

        for(String s : names)
            System.out.println(s);

        return null;
    }

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
        } catch (ResponseException e) {
            log.error("Http status: {}, Message: {}", e.getHttpStatus().value(), e.getMessage());
            return MakeResp.make(e.getHttpStatus(), e.getMessage());
        }

        JSONArray clipArray = (JSONArray) jsonObject.get("data");
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
        try {
            jsonObject = callTwitchAPI.requestOauth(code);

            if(jsonObject == null) {
                return new ResponseEntity<>(new CommonResponseDto(HttpStatus.BAD_REQUEST, "Invalid Code")
                        , HttpStatus.BAD_REQUEST);
            }

            String accessToken = jsonObject.getString("access_token");
            String refreshToken = jsonObject.getString("refresh_token");

            jsonObject = callTwitchAPI.requestUserByToken(accessToken);
            log.info("Twitch Response: {}", jsonObject);

            long twitchId = jsonObject.getJSONArray("data").getJSONObject(0).getLong("id");
            log.info("Twitch Id: " + twitchId);

            AccountEntity entity = new AccountEntity();
            entity.setId(twitchId);
            entity.setTwitchAccessToken(accessToken);
            entity.setTwitchRefreshToken(refreshToken);
            accountService.save(entity);

            String token = JwtTokenProvider.generateToken(entity.getId());
            log.info("token: {}", token);
            log.info("id: {}", JwtTokenProvider.getId(token));
            response.setHeader("Authorization", "Bearer " + token);

        } catch (IOException e) {
            log.error("Fail to request twitch clips");
            e.printStackTrace();
            return MakeResp.make(HttpStatus.INTERNAL_SERVER_ERROR, "Server error");
        }

        return MakeResp.make(HttpStatus.OK, "Success");
    }
}