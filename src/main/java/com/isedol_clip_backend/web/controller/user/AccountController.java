package com.isedol_clip_backend.web.controller.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.isedol_clip_backend.exception.ApiRequestException;
import com.isedol_clip_backend.exception.NoExistedDataException;
import com.isedol_clip_backend.util.CallTwitchAPI;
import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.web.model.TwitchUser;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class AccountController {

    private final CallTwitchAPI callTwitchApi;
    private final ObjectMapper objectMapperSe;

    @GetMapping("")
    public ResponseEntity<CommonResponse> getUserByToken() throws IOException, ApiRequestException, NoExistedDataException {

        long id = getAccountId();
        log.info("id: {}", id);
        JSONArray jsonArray = callTwitchApi.requestUser(new long[]{id}, null);
        String jsonString = jsonArray.getJSONObject(0).toString();
        TwitchUser user = objectMapperSe.readValue(jsonString, TwitchUser.class);

        return MakeResp.make(HttpStatus.OK, "Success", user);
    }

    @GetMapping("/verify")
    public ResponseEntity<CommonResponse> verify() {
        return MakeResp.make(HttpStatus.OK, "USER");
    }

    private long getAccountId() {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        return Long.parseLong(id);
    }
}
