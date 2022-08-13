package com.isedol_clip_backend.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Getter
public class LoadSecret {
    @Value("${secret.path}")
    private String secretPath;
    private String twitchClientId;
    private String twitchSecret;
    private String jwtSecret;
    private String twitchAccessToken;

    public void load() {
        log.info("secretPath: {}", secretPath);

        JSONObject jsonObject;
        try {
            String FileData = FileUtil.getDataFromFilePath(secretPath);
            jsonObject = new JSONObject(FileData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        twitchClientId = jsonObject.getString("twitch_client_id");
        twitchSecret = jsonObject.getString("twitch_secret");
        twitchAccessToken = jsonObject.getString("twitch_access_token");
        jwtSecret = jsonObject.getString("jwt_secret");

        log.info("twitchClientId: {}", twitchClientId);
        log.info("twitchSecret: {}", twitchSecret);
        log.info("twitchAccessToken: {}", twitchAccessToken);
        log.info("jwtSecret: {}", jwtSecret);
    }
}
