package com.isedol_clip_backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isedol_clip_backend.web.model.TwitchClip;
import com.isedol_clip_backend.web.model.TwitchUser;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TwitchMapper {

    private final ObjectMapper objectMapperSe;

    public TwitchUser[] mappingUsers(JSONArray jsonArray) throws JsonProcessingException {
        TwitchUser[] users = new TwitchUser[jsonArray.length()];
        for(int i=0; i<jsonArray.length(); i++) {
            users[i] = objectMapperSe.readValue(jsonArray.getJSONObject(i).toString(), TwitchUser.class);
        }

        return users;
    }

    public TwitchUser mappingUser(JSONArray jsonArray) throws JsonProcessingException {
        TwitchUser user = objectMapperSe.readValue(jsonArray.getJSONObject(0).toString(), TwitchUser.class);

        return user;
    }

    public TwitchClip[] mappingClips(JSONObject jsonObject) throws JsonProcessingException {
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        TwitchClip[] clips = new TwitchClip[jsonArray.length()];
        for(int i=0; i<jsonArray.length(); i++) {
            clips[i] = objectMapperSe.readValue(jsonArray.getJSONObject(i).toString(), TwitchClip.class);
        }

        return clips;
    }
}
