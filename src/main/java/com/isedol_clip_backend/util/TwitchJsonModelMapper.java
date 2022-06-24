package com.isedol_clip_backend.util;

import com.isedol_clip_backend.web.model.TwitchClip;
import com.isedol_clip_backend.web.model.TwitchUser;
import org.json.JSONArray;
import org.json.JSONObject;

// 트위치 응답 Json을 Response에 들어갈 dto로 매핑
public class TwitchJsonModelMapper {
    public static TwitchUser[] userMapping(JSONObject twitchJson) {
        JSONArray jsonArray = twitchJson.getJSONArray("data");
        TwitchUser[] users = new TwitchUser[jsonArray.length()];

        for(int i=0; i<users.length; i++) {
            TwitchUser user = new TwitchUser();
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            user.setId(jsonObject.getString("id"));
            user.setLogin(jsonObject.getString("login"));
            user.setDisplayName(jsonObject.getString("display_name"));
            user.setProfileImageUrl(jsonObject.getString("profile_image_url"));

            users[i] = user;
        }

        return users;
    }

    public static TwitchClip[] clipMapping(JSONObject twitcjJson) {
        JSONArray jsonArray = twitcjJson.getJSONArray("data");
        TwitchClip[] clips = new TwitchClip[jsonArray.length()];

        for(int i=0; i<clips.length; i++) {
            TwitchClip clip = new TwitchClip();
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            clip.setId(jsonObject.getString("id"));
            clip.setTitle(jsonObject.getString("title"));
            clip.setCreatedAt(jsonObject.getString("created_at"));
            clip.setDuration(jsonObject.getDouble("duration"));
            clip.setCreatorName(jsonObject.getString("creator_name"));
            clip.setEmbedUrl(jsonObject.getString("embed_url"));
            clip.setThumbnailUrl(jsonObject.getString("thumbnail_url"));
            clip.setViewCount(jsonObject.getInt("view_count"));

            clips[i] = clip;
        }

        return clips;
    }
}
