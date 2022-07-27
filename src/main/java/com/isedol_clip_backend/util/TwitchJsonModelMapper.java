package com.isedol_clip_backend.util;

import com.isedol_clip_backend.web.model.TwitchClip;
import com.isedol_clip_backend.web.model.TwitchUser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;

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

    public static TwitchClip[] clipMapping(JSONObject twitcjJson) throws ParseException {
        JSONArray jsonArray = twitcjJson.getJSONArray("data");
        TwitchClip[] clips = new TwitchClip[jsonArray.length()];

        for(int i=0; i<clips.length; i++) {
            TwitchClip clip = new TwitchClip();
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String createdAt = ConvertCalender
                    .rfcToGeneral(jsonObject.getString("created_at"));

            clip.setId(jsonObject.getString("id"));
            clip.setTitle(jsonObject.getString("title"));
            clip.setCreatedAt(createdAt);
            clip.setDuration(jsonObject.getDouble("duration"));
            clip.setCreatorName(jsonObject.getString("creator_name"));
            clip.setUrl(jsonObject.getString("url"));
            clip.setEmbedUrl(jsonObject.getString("embed_url"));
            clip.setThumbnailUrl(jsonObject.getString("thumbnail_url"));
            clip.setViewCount(jsonObject.getInt("view_count"));
            clip.setBroadcasterId(jsonObject.getString("broadcaster_id"));
            clip.setBroadcasterName(jsonObject.getString("broadcaster_name"));
            clip.setVideoId(jsonObject.getString("video_id"));

            if(!jsonObject.isNull("vod_offset"))
                clip.setVodOffset((jsonObject.getInt("vod_offset")));


            clips[i] = clip;
        }

        return clips;
    }
}
