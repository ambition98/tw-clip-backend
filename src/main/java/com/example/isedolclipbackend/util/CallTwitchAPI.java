package com.example.isedolclipbackend.util;

import com.example.isedolclipbackend.web.model.request.TwitchClipRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class CallTwitchAPI {

    // https://dev.twitch.tv/docs/api/reference#get-users
    public JSONObject requestUserById(String id) throws IOException {
        URL url = new URL("https://api.twitch.tv/helix/users?login=" + id);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + LoadSecret.twitchSecret);
        conn.setRequestProperty("client-id", LoadSecret.twitchClientId);

        JSONObject jsonObject = getResponse(conn);
        log.info("Response twitch user: " + jsonObject);

        return jsonObject;
    }

    public JSONObject requestUserByToken(String token) throws IOException {
        URL url = new URL("https://api.twitch.tv/helix/users");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("client-id", LoadSecret.twitchClientId);

        JSONObject jsonObject = getResponse(conn);
        log.info("Response twitch user: " + jsonObject);

        return jsonObject;
    }

    //https://dev.twitch.tv/docs/api/reference#get-clips
    public JSONObject requestClips(TwitchClipRequestDto dto) throws IOException {
        URL url = makeRequestClipsUrl(dto);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Client-Id", LoadSecret.twitchClientId);
        conn.setRequestProperty("Authorization", "Bearer" + LoadSecret.twitchSecret);

        JSONObject jsonObject = getResponse(conn);
        log.info("Response twitch clips: " + jsonObject);

        return jsonObject;
    }

    // https://dev.twitch.tv/docs/authentication/getting-tokens-oauth#authorization-code-grant-flow
    public JSONObject requestOauth(String code) throws IOException {
        URL url = new URL("https://id.twitch.tv/oauth2/token");

        String parameters = "client_id="+LoadSecret.twitchClientId +
                "&client_secret="+LoadSecret.twitchSecret +
                "&code="+ code +
                "&grant_type=authorization_code" +
                "&redirect_uri=http://localhost:8080/isedol-clip/after-login";

        byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
        conn.setUseCaches(false);

        DataOutputStream output = new DataOutputStream(conn.getOutputStream());
        output.write(postData);
        output.flush();
        output.close();

        JSONObject jsonObject = getResponse(conn);
        log.info("Response twitch token: " + jsonObject);

        return jsonObject;
    }

    private String convertResponseToString(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line;
        while((line = br.readLine()) != null) {
            sb.append(line);
        }

        return sb.toString();
    }

    private JSONObject getResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        JSONObject jsonObject;

        if(responseCode < HttpURLConnection.HTTP_BAD_REQUEST) {
            jsonObject = new JSONObject(convertResponseToString(conn.getInputStream()));
        } else {
            String errorData = convertResponseToString(conn.getErrorStream());
//            log.warn(responseCode + " " + conn.getResponseMessage() + " " + errorData);

            jsonObject = new JSONObject();
            jsonObject.put("code", responseCode);
            jsonObject.put("message", conn.getResponseMessage());
            jsonObject.put("response", errorData);
        }

        return jsonObject;
    }

    private URL makeRequestClipsUrl(TwitchClipRequestDto dto) throws MalformedURLException {
        StringBuilder sb = new StringBuilder("https://api.twitch.tv/helix/clips?");
        sb.append("broadcaster_id=").append(dto.getBroadcasterId()).append("&");

        if(dto.getAfter() != null) {
            sb.append("after=").append(dto.getAfter()).append("&");
        } else if(dto.getStartedAt() != null) {
            sb.append("started_at=").append(dto.getStartedAt()).append("&");
        } else if(dto.getEndedAt() != null) {
            sb.append("ended_at=").append(dto.getEndedAt()).append("&");
        } else if(dto.getFirst() != null) {
            sb.append("first=").append(dto.getFirst()).append("&");
        }

        sb.deleteCharAt(sb.length() - 1);
        log.info("url: " + sb);

        return new URL(sb.toString());
    }
}
