package com.isedol_clip_backend.web.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class CommonResponseDto {
    private HttpStatus status;
    private String msg;
//    private String data;
    private ResponseData data;

    public CommonResponseDto(HttpStatus status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public CommonResponseDto(HttpStatus status, String msg, ResponseData data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }
}
