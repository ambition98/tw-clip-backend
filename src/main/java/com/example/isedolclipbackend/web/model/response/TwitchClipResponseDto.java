package com.example.isedolclipbackend.web.model.response;

import com.example.isedolclipbackend.web.model.TwitchClip;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TwitchClipResponseDto implements ResponseData {
    private TwitchClip[] clips;
    private String cursor;
}
