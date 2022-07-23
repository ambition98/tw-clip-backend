package com.isedol_clip_backend.web.model.response;

import com.isedol_clip_backend.web.model.TwitchUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class RespUser {
    private String accessToken;
    private TwitchUser twitchUser;
}
