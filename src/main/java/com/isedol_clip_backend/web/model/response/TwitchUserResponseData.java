package com.isedol_clip_backend.web.model.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TwitchUserResponseData implements ResponseData {
    private String id;
    private String login;
    private String displayName;
    private String profileImageUrl;

    /* Twitch Api로부터 오는 전체 필드 (당장은 필요없음) */
//    private String type;
//    private String broadcasterType;
//    private String description;
//    private String offlineImageUrl;
//    private int viewCount;
//    private String createdAt;
}
