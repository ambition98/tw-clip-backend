package com.isedol_clip_backend.web.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommonResponse {
    private String httpStatus;
    private String msg;
    private Object dto;

    public CommonResponse(String httpStatus, String msg) {
        this.httpStatus = httpStatus;
        this.msg = msg;
    }

    public CommonResponse(String httpStatus, String msg, Object dto) {
        this.httpStatus = httpStatus;
        this.msg = msg;
        this.dto = dto;
    }
}
