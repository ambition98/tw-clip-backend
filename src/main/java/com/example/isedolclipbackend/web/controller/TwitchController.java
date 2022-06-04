package com.example.isedolclipbackend.web.controller;

import com.example.isedolclipbackend.auth.AuthToken;
import com.example.isedolclipbackend.util.CallTwitchAPI;
import com.example.isedolclipbackend.util.JwtTokenProvider;
import com.example.isedolclipbackend.web.entity.AccountEntity;
import com.example.isedolclipbackend.web.model.TwitchClip;
import com.example.isedolclipbackend.web.model.request.TwitchClipRequestDto;
import com.example.isedolclipbackend.web.model.response.CommonResponseDto;
import com.example.isedolclipbackend.web.model.response.TwitchClipResponseDto;
import com.example.isedolclipbackend.web.model.response.TwitchUserResponseDto;
import com.example.isedolclipbackend.web.service.AccountService;
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
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/user/{id}")
    public TwitchUserResponseDto getTwitchUser(@PathVariable @NonNull final String id) {
        JSONObject jsonObject = null;

        try {
            jsonObject = callTwitchAPI.requestUserById(id);
        } catch (IOException e) {
            log.error("Fail to request twitch user");
            e.printStackTrace();
            return null;
        }

        jsonObject = (JSONObject) ((JSONArray)jsonObject.get("data")).get(0);
        TwitchUserResponseDto dto = new TwitchUserResponseDto();
        dto.setId((String) jsonObject.get("id"));
        dto.setLogin((String) jsonObject.get("login"));
        dto.setDisplayName((String) jsonObject.get("display_name"));
        dto.setProfileImageUrl((String) jsonObject.get("profile_image_url"));

        log.info(dto.toString());

        return dto;
    }

    @GetMapping("/clips")
    public TwitchClipResponseDto getTwitchClips(final TwitchClipRequestDto requestDto) {
        log.info("RequestDto: " + requestDto.toString());
        JSONObject jsonObject = null;

        try {
            jsonObject = callTwitchAPI.requestClips(requestDto);
        } catch (IOException e) {
            log.error("Fail to request twitch clips");
            e.printStackTrace();
            return null;
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

        TwitchClipResponseDto responseDto = new TwitchClipResponseDto();
        responseDto.setClips(clips);
        responseDto.setCursor(cursor);

        return responseDto;
    }


    @GetMapping("/oauth")
    public ResponseEntity<CommonResponseDto> getTwitchOauth(@NonNull final String code, HttpServletResponse response) {
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

            AuthToken authToken = jwtTokenProvider.generateToken(entity.getId());
            log.info("token: {}", authToken.getToken());
            log.info("id: {}", authToken.getUserId());
            response.setHeader("Authorization", "Bearer " + authToken.getToken());
//            if(accountService.existsById(twitchId)) {
//                log.info("Exited Id");
//
////                entity = accountService.getById(twitchId);
////                entity.setTwitchAccessToken(accessToken);
////                entity.setTwitchRefreshToken(refreshToken);
////                accountService.save(entity);
//
//            } else {
//                log.info("Not exist Id");
//
////                entity = new AccountEntity();
////                entity.setId(twitchId);
////                entity.setTwitchAccessToken(accessToken);
////                entity.setTwitchRefreshToken(refreshToken);
////                entity = accountService.save(entity);
////                AuthToken authToken = jwtTokenProvider.generateToken(entity.getId());
////                log.info("token: {}", authToken.getToken());
////                log.info("id: {}", authToken.getUserId());
//
////                System.out.println(accountService.getAccessTokenById(entity.getId()));
//
////                log.info(String.valueOf(accountService.existsById(entity.getId())));
//            }

        } catch (IOException e) {
            log.error("Fail to request oauth token");
            e.printStackTrace();
        }

        return new ResponseEntity<>(new CommonResponseDto(HttpStatus.OK, "Success Generate Token"), HttpStatus.OK);
    }
}