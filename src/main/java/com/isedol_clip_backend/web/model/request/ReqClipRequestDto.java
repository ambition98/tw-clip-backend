package com.isedol_clip_backend.web.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
public class ReqClipRequestDto {
    @NotBlank(message = "Required Parameter. String login")
    private String login; // 트위치 닉네임
    private String broadcasterId;
    private String after; // cursor
    private int first; // count
    private String endedAt;
    private String startedAt;
}
