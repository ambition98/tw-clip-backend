package com.example.isedolclipbackend.web.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TwitchClipRequestDto {
    private int broadcasterId;
    private String after; // cursor
    private String first; // count
    private String endedAt;
    private String startedAt;
}
