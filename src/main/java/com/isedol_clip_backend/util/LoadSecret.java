package com.isedol_clip_backend.util;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;

@Slf4j
@Component
public class LoadSecret {
    public static final String twitchClientId;
    public static final String twitchSecret;
    public static final String jwtSecret;
    public static final String twitchAccessToken;

    static {
        ClassPathResource resource = new ClassPathResource("secret/secret.json");
        if(!resource.exists()) {
            log.warn("secret.json file does not exist!!!");
        }

        String data = "";
        try {
            byte[] byteData = FileCopyUtils.copyToByteArray(resource.getInputStream());
            data = new String(byteData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        JSONObject jsonObject;

        jsonObject = new JSONObject(data);

        twitchClientId = jsonObject.getString("twitch_client_id");
        twitchSecret = jsonObject.getString("twitch_secret");
        twitchAccessToken = jsonObject.getString("twitch_access_token");
        jwtSecret = jsonObject.getString("jwt_secret");

//        log.info("twitch_cliend_id: {}", twitchClientId);
//        log.info("twitch_secret: {}", twitchSecret);
//        log.info("twitch_access_token: {}", twitchAccessToken);
//        log.info("jwt_secret: {}", jwtSecret);
    }
}
