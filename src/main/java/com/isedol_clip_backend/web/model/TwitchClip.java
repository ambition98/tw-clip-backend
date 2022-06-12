package com.isedol_clip_backend.web.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TwitchClip {
    private String id;
    private String embedUrl;
    private String creatorName;
    private String title;
    private int viewCount;
    private String createdAt;
    private String thumbnailUrl;
    private double duration;

    /* Twitch Api로부터 오는 전체 필드 (당장은 필요없음) */
//    private String url;
//    private String videoId;
//    private String gameId;
//    private String language;
//    private String broadcasterId;
//    private String broadcasterName;
//    private String creatorId;
}
