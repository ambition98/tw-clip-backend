package com.isedol_clip_backend.web.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommonResponseDto {
    private String httpStatus;
    private String msg;
//    private String data;
    private ResponseData data;

    public CommonResponseDto(String httpStatus, String msg) {
        this.httpStatus = httpStatus;
        this.msg = msg;
    }

    public CommonResponseDto(String httpStatus, String msg, ResponseData data) {
        this.httpStatus = httpStatus;
        this.msg = msg;
        this.data = data;
    }
}
