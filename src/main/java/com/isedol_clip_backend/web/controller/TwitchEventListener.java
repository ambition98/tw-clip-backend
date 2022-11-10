package com.isedol_clip_backend.web.controller;

import com.isedol_clip_backend.util.LoadSecret;
import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/sub")
public class TwitchEventListener {

    private final LoadSecret loadSecret;

    @PostMapping("/callback")
    @ResponseBody
    public String twitchCallback(HttpServletResponse response, @RequestBody String body) {
        response.setStatus(200);
        log.info("------------- callback -------------");
        log.info(body);
        JSONObject json = new JSONObject(body);

        return json.getString("challenge");
    }

    @GetMapping("/show")
    public ResponseEntity<CommonResponse> showSub() throws IOException {
        return MakeResp.make(HttpStatus.OK, "Success", getSubIdList());
    }

    @GetMapping("/delete")
    public void cancelAllSub() throws IOException, InterruptedException {
        List<String> idList = getSubIdList();
        for (String id : idList) {
            cancelSub(id);
            Thread.sleep(500);
        }
    }

    @GetMapping("/trysub")
    public void trysub() {
//        Object[] id = twitchStorage.getIsedolInfo().keySet().toArray();
        try {
            URL url = new URL("https://api.twitch.tv/helix/eventsub/subscriptions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            StringBuilder sb = new StringBuilder();

            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            setAuthHeader(conn);
            conn.setRequestProperty("Content-Type", "application/json");

            DataOutputStream output = new DataOutputStream(conn.getOutputStream());
            byte[] body = makeSubReqBody(SubType.online, "46137589");
            output.write(body);
            output.flush();
            output.close();

            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            int status = conn.getResponseCode();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            log.info("------------- response -------------");
            log.info("status: {}", status);
            log.info(sb.toString());



        } catch (IOException e) {
            log.error("Connection Error");
            e.printStackTrace();
        }
    }

    private int cancelSub(String id) throws IOException {
//        StringBuilder sb = new StringBuilder(""https://api.twitch.tv/helix/eventsub/subscriptions"?");
//        for (String id : idList)
//            sb.append("id=").append(id).append("&");
//        sb.deleteCharAt(sb.length() - 1);

//        URL url = new URL(sb.toString());
        URL url = new URL("https://api.twitch.tv/helix/eventsub/subscriptions?id=" + id);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        setAuthHeader(conn);
        conn.setRequestMethod("DELETE");

        int status = conn.getResponseCode();
        log.info("cancelSub status: {}", status);
        return status;
    }
    private List<String> getSubIdList() throws IOException {
        StringBuilder sb = new StringBuilder();
        URL url = new URL("https://api.twitch.tv/helix/eventsub/subscriptions");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        setAuthHeader(conn);
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        log.info("Current Sub: {}", sb.toString());
        JSONObject json = new JSONObject(sb.toString());
        JSONArray jsonArray = json.getJSONArray("data");
        List<String> idList = new ArrayList<>(jsonArray.length());

        for (int i=0; i<jsonArray.length(); i++) {
            JSONObject data = jsonArray.getJSONObject(i);
            idList.add(data.getString("id"));
        }

        log.info(idList.toString());
        return idList;
    }
    private byte[] makeSubReqBody(SubType type, String bid) {
        JSONObject json = new JSONObject();
        JSONObject condition = new JSONObject();
        JSONObject transport = new JSONObject();

        condition.put("broadcaster_user_id", bid);

        transport.put("method", "webhook");
        transport.put("callback", "https://danyleee.com/sub/callback");
        transport.put("secret", "testsecret");

        json.put("type", type.getType());
        json.put("version", "1");
        json.put("condition", condition);
        json.put("transport", transport);

        log.info(json.toString());
        return json.toString().getBytes();
    }

    private void setAuthHeader(HttpURLConnection conn) {
        conn.setRequestProperty("Authorization", "Bearer " + loadSecret.getTwitchAccessToken());
        conn.setRequestProperty("Client-Id", loadSecret.getTwitchClientId());
    }

    private enum SubType {
        follow("channel.follow"),
        online("stream.online");

        private final String type;

        SubType(String type) { this.type = type; }

        public String getType() { return type; }
    }
}
