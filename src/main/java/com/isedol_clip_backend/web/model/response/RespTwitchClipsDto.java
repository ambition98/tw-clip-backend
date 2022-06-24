package com.isedol_clip_backend.web.model.response;

import com.isedol_clip_backend.web.model.TwitchClip;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RespTwitchClipsDto {
    private TwitchClip[] clips;
    private String cursor;
}
