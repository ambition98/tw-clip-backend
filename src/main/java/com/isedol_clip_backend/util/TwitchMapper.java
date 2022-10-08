package com.isedol_clip_backend.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isedol_clip_backend.web.model.TwitchClip;
import com.isedol_clip_backend.web.model.TwitchUser;
import lombok.RequiredArgsConstructor;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class TwitchMapper {

    private final ObjectMapper objectMapperSe;

    private static final Set<String> BANNED_CLIPS = new HashSet<>(Arrays.asList(
            "SpoopyRockyHamPraiseIt-_mbCCsQMqjYcaLre",
            "SpicyPlausibleDiamondRalpherZ-M4mzvu_rtXFrR0cg"
    ));

    public TwitchUser[] mappingUsers(JSONArray jsonArray) throws JsonProcessingException {
        TwitchUser[] users = new TwitchUser[jsonArray.length()];
        for (int i=0; i<jsonArray.length(); i++) {
            users[i] = objectMapperSe.readValue(jsonArray.getJSONObject(i).toString(), TwitchUser.class);
        }

        return users;
    }

    public TwitchUser mappingUser(JSONArray jsonArray) throws JsonProcessingException {

        return objectMapperSe.readValue(jsonArray.getJSONObject(0).toString(), TwitchUser.class);
    }

    public List<TwitchClip> mappingClips(JSONObject jsonObject) throws JsonProcessingException, ParseException {
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        List<TwitchClip> clips = new ArrayList<>(jsonArray.length());
        for (int i=0; i<jsonArray.length(); i++) {
            TwitchClip temp = objectMapperSe.readValue(jsonArray.getJSONObject(i).toString(), TwitchClip.class);

            if (BANNED_CLIPS.contains(temp.getId()))
                continue;

            String createdAt = temp.getCreatedAt();
            temp.setCreatedAt(ConvertDateFormat.rfcToGeneral(createdAt));
            clips.add(temp);
        }

        return clips;
    }
}
