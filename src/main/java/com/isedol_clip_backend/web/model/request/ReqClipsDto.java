package com.isedol_clip_backend.web.model.request;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReqClipsDto {
    @NotBlank(message = "Required Parameter. String login")
    private String broadcasterId;
    private String after; // cursor
    private int first; // count
    private String startedAt;
    private String endedAt;
}
