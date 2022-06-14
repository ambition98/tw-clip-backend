package com.isedol_clip_backend.util;

import com.isedol_clip_backend.exception.ResponseException;
import com.isedol_clip_backend.web.model.request.ClipRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
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
    public JSONObject requestUserById(String id) throws IOException, ResponseException {
        URL url = new URL("https://api.twitch.tv/helix/users?id=" + id);

//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestProperty("Authorization", "Bearer " + LoadSecret.twitchSecret);
//        conn.setRequestProperty("Client-Id", LoadSecret.twitchClientId);
//
//        JSONObject jsonObject = getResponse(conn);
//        log.info("Response twitch user: " + jsonObject);
//        checkResponseStatus(jsonObject);
//
//        return jsonObject;
        return requestUser(url);
    }

    public JSONObject requestUserByName(String name) throws IOException, ResponseException {
        URL url = new URL("https://api.twitch.tv/helix/users?login=" + name);

//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setRequestProperty("Authorization", "Bearer " + LoadSecret.twitchSecret);
//        conn.setRequestProperty("Client-Id", LoadSecret.twitchClientId);
//
//        JSONObject jsonObject = getResponse(conn);
//        log.info("Response twitch user: " + jsonObject);
//        checkResponseStatus(jsonObject);

//        return jsonObject;

        return requestUser(url);
    }

    private JSONObject requestUser(URL url) throws IOException, ResponseException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + LoadSecret.twitchAccessToken);
        conn.setRequestProperty("Client-Id", LoadSecret.twitchClientId);

        checkResponseStatus(conn);
        JSONObject jsonObject = getResponse(conn);
        log.info("Response twitch user: " + jsonObject);

        return jsonObject;
    }

    public JSONObject requestUserByToken(String token) throws IOException, ResponseException {
        URL url = new URL("https://api.twitch.tv/helix/users");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("Client-Id", LoadSecret.twitchClientId);

        checkResponseStatus(conn);
        JSONObject jsonObject = getResponse(conn);
        log.info("Response twitch user: " + jsonObject);

        return jsonObject;
    }

    //https://dev.twitch.tv/docs/api/reference#get-clips
    public JSONObject requestClips(ClipRequestDto dto) throws IOException, ResponseException {
        URL url = makeRequestClipsUrl(dto);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + LoadSecret.twitchAccessToken);
        conn.setRequestProperty("Client-Id", LoadSecret.twitchClientId);

        checkResponseStatus(conn);
        JSONObject jsonObject = getResponse(conn);
        log.info("Response twitch clips: " + jsonObject);

        return jsonObject;
    }

    // https://dev.twitch.tv/docs/authentication/getting-tokens-oauth#authorization-code-grant-flow
    public JSONObject requestOauth(String code) throws IOException, ResponseException {
        URL url = new URL("https://id.twitch.tv/oauth2/token");

        String parameters = "client_id="+LoadSecret.twitchClientId +
                "&client_secret="+LoadSecret.twitchSecret +
                "&code="+ code +
                "&grant_type=authorization_code" +
                "&redirect_uri=http://localhost:8080/isedol-clip/after-login";

        byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        requestPostMethod(conn, postData);
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

//        checkResponseStatus(conn);
        JSONObject jsonObject = getResponse(conn);
        log.info("Response twitch token: " + jsonObject);

        return jsonObject;
    }

    // App access token. 즉, 서버측의 토큰이지 사용자의 토큰 아님
    // https://dev.twitch.tv/docs/authentication/validate-tokens
    public boolean checkAccessTokenValidation() throws IOException {
        URL url = new URL("https://id.twitch.tv/oauth2/validate");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + LoadSecret.twitchAccessToken);

        int ResponseCode = conn.getResponseCode();
        if(ResponseCode == 200) {
            log.info("App Access Token Validation: {}, Access token is valid", ResponseCode);
            return true;
        } else {
            log.info("App Access Token Validation: {}, Access token is invalid. Need Reissue", ResponseCode);
            return false;
        }
    }

    // https://dev.twitch.tv/docs/authentication/getting-tokens-oauth/#client-credentials-grant-flow
    public boolean requestAccessToken() throws IOException {
        URL url = new URL("https://id.twitch.tv/oauth2/token");

        String parameters = "client_id=" + LoadSecret.twitchClientId +
                "&client_secret=" + LoadSecret.twitchSecret +
                "grant_type=client_credentials";

        byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        requestPostMethod(conn, postData);


//        conn.setDoOutput(true);
//        conn.setInstanceFollowRedirects(false);
//        conn.setRequestMethod("POST");
//        conn.setRequestProperty("charset", "utf-8");
//        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//        conn.setRequestProperty("Content-Length", Integer.toString(postData.length));
//        conn.setUseCaches(false);
//
//        DataOutputStream output = new DataOutputStream(conn.getOutputStream());
//        output.write(postData);
//        output.flush();
//        output.close();

        JSONObject jsonObject = getResponse(conn);
        log.info("Response twitch access token: {}", jsonObject);

        return true;
    }

    private void requestPostMethod(HttpURLConnection conn, byte[] postData) throws IOException {
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

    private URL makeRequestClipsUrl(ClipRequestDto dto) throws MalformedURLException {
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

    private void checkResponseStatus(HttpURLConnection conn) throws IOException, ResponseException {
        int status = conn.getResponseCode();
        if(status != 200) {
            JSONObject jsonObject = getResponse(conn);
            log.error("Response Data: {}", jsonObject.toString());
            throw new ResponseException(jsonObject.getString("message"), HttpStatus.resolve(status));
        }

//        HttpStatus httpStatus = null;
//        try {
//            httpStatus = HttpStatus.resolve(jsonObject.getInt("code"));
//        } catch (JSONException e) {
//            return;
//        }
//
//        if(httpStatus.value() >= 400) {
//            throw new ResponseException(jsonObject.getString("message"), httpStatus);
//        }
    }
}
