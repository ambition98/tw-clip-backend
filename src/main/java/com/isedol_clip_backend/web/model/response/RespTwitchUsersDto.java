package com.isedol_clip_backend.web.model.response;

import com.isedol_clip_backend.web.model.TwitchUser;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RespTwitchUsersDto {
    private TwitchUser[] users;
}
