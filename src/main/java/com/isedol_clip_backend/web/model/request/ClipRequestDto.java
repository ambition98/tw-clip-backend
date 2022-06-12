package com.isedol_clip_backend.web.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ClipRequestDto {
    private int broadcasterId;
    private String after; // cursor
    private String first; // count
    private String endedAt;
    private String startedAt;
}
