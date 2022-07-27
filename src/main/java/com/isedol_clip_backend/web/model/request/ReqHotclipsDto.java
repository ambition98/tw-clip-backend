package com.isedol_clip_backend.web.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReqHotclipsDto {
    private int first;
    private String[] after;
}
