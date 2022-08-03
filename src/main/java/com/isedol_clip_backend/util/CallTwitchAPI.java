package com.isedol_clip_backend.util;

import com.isedol_clip_backend.exception.RequestException;
import com.isedol_clip_backend.web.model.request.ReqClipsDto;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;

@Slf4j
@Component
public class CallTwitchAPI {

    // https://dev.twitch.tv/docs/api/reference#get-users
    public JSONArray requestUser(long[] id, String[] login) throws IOException, RequestException {
        URL url = makeRequestUsersUrl(id, login);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        setAuthHeader(conn);

        JSONObject jsonObject = getResponse(conn);
        checkEmptyData(jsonObject);
        log.info("Response twitch user: " + jsonObject);

        return jsonObject.getJSONArray("data");
    }

    public JSONObject requestUserByToken(String token) throws IOException, RequestException {
        URL url = new URL("https://api.twitch.tv/helix/users");
        log.info("Twitch URL: {}", url);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + token);
        conn.setRequestProperty("Client-Id", LoadSecret.twitchClientId);

        JSONObject jsonObject = getResponse(conn);
        checkEmptyData(jsonObject);

        log.info("Response twitch token: " + jsonObject);

        return jsonObject;
    }

    //https://dev.twitch.tv/docs/api/reference#get-clips
//    @CheckRunningTime
    public JSONObject requestClips(ReqClipsDto dto) throws IOException, RequestException, ParseException {
        URL url = makeRequestClipsUrl(dto);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestProperty("Authorization", "Bearer " + LoadSecret.twitchAccessToken);
        conn.setRequestProperty("Client-Id", LoadSecret.twitchClientId);

        JSONObject jsonObject = getResponse(conn);
        checkEmptyData(jsonObject);

        return jsonObject;
    }

    public JSONObject searchUser(String keyword) throws IOException, RequestException {
        URL url = new URL("https://api.twitch.tv/helix/search/channels?first=100&query=" + keyword);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        setAuthHeader(conn);

        return getResponse(conn);
    }

    // https://dev.twitch.tv/docs/authentication/getting-tokens-oauth#authorization-code-grant-flow
    public JSONObject requestOauth(String code) throws IOException, RequestException {
        URL url = new URL("https://id.twitch.tv/oauth2/token");
        log.info("Twitch API URL: {}", url);

        String parameters = "client_id="+LoadSecret.twitchClientId +
                "&client_secret="+LoadSecret.twitchSecret +
                "&code="+ code +
                "&grant_type=authorization_code" +
                "&redirect_uri=http://localhost:8080/afterlogin";

        byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        requestPostMethod(conn, postData);

        JSONObject jsonObject = getResponse(conn);
        log.info("Response twitch token: " + jsonObject);

        return jsonObject;
    }

    // App access token. 즉, 서버측의 토큰이지 사용자의 토큰 아님
    // https://dev.twitch.tv/docs/authentication/validate-tokens
    public boolean checkAccessTokenValidation() throws IOException {
        URL url = new URL("https://id.twitch.tv/oauth2/validate");
        log.info("Twitch API URL: {}", url);

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
    public boolean requestAccessToken() throws IOException, RequestException {
        URL url = new URL("https://id.twitch.tv/oauth2/token");

        String parameters = "client_id=" + LoadSecret.twitchClientId +
                "&client_secret=" + LoadSecret.twitchSecret +
                "grant_type=client_credentials";

        byte[] postData = parameters.getBytes(StandardCharsets.UTF_8);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        requestPostMethod(conn, postData);

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

    // Twitch Api가 인증 관련 오류가 아니면 무조건 200을 응답한다.
    // 예를들어 존재하지 않는 id로 계정 정보를 요청해도 200을 응답한다.
    // 때문에 Http status 체크와, 빈 데이터 체크 둘 다 있어야 한다.
    private JSONObject getResponse(HttpURLConnection conn) throws IOException, RequestException {
        int status = conn.getResponseCode();
        JSONObject jsonObject;

        if(status != 200) {
            jsonObject = convertResponseToJson(conn.getErrorStream());
            log.error("Error reseponse: {}", jsonObject);
            throw new RequestException(jsonObject.getString("message"), HttpStatus.resolve(status));
        }

        jsonObject = convertResponseToJson(conn.getInputStream());
//        log.info("Ok reseponse: {}", jsonObject);

        return jsonObject;
    }

    // 정상 응답을 json으로 변경하여 리턴
    private JSONObject convertResponseToJson(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line;
        while((line = br.readLine()) != null) {
            sb.append(line);
        }

        return new JSONObject(sb.toString());
    }

    // 해당 메서드는 일부 api요청에만 적용됨 (정상 응답시 "data" JsonArray로 응답을 하는 api)
    private void checkEmptyData(JSONObject jsonObject) throws RequestException {
        if(jsonObject.getJSONArray("data").length() < 1) {
            throw new RequestException("No Content", HttpStatus.OK);
        }
    }

    private URL makeRequestClipsUrl(ReqClipsDto dto) throws MalformedURLException, ParseException {
        StringBuilder sb = new StringBuilder("https://api.twitch.tv/helix/clips?");
        sb.append("broadcaster_id=").append(dto.getBroadcasterId()).append("&");

        if(dto.getAfter() != null) {
            sb.append("after=").append(dto.getAfter()).append("&");
        }

        if(dto.getStartedAt() != null) {
            String startedAt =
                    ConvertCalender.generalToRfc(dto.getStartedAt(),
                            ConvertCalender.convertType.START_AT);
            sb.append("started_at=").append(startedAt).append("&");
        }

        if(dto.getEndedAt() != null) {
            String endedAt = ConvertCalender.generalToRfc(dto.getEndedAt(),
                    ConvertCalender.convertType.ENDED_AT);
            sb.append("ended_at=").append(endedAt).append("&");
        }

        if(dto.getFirst() != 0) {
            sb.append("first=").append(dto.getFirst()).append("&");
        }

        sb.deleteCharAt(sb.length() - 1);
//        log.info("Twitch API URL: " + sb);

        return new URL(sb.toString());
    }

    private URL makeRequestUsersUrl(long[] id, String[] login) throws MalformedURLException {
        StringBuilder sb = new StringBuilder();

        if(id != null) {
            for(long i : id) {
                sb.append("id=").append(i).append("&");
            }
        }

        if(login != null) {
            for(String s : login) {
                sb.append("login=").append(s).append("&");
            }
        }

        sb.deleteCharAt(sb.length() - 1);

        return new URL("https://api.twitch.tv/helix/users?" + sb);
    }

    private void setAuthHeader(HttpURLConnection conn) {
        conn.setRequestProperty("Authorization", "Bearer " + LoadSecret.twitchAccessToken);
        conn.setRequestProperty("Client-Id", LoadSecret.twitchClientId);
    }
}
