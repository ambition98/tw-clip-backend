package com.isedol_clip_backend.util;

import com.isedol_clip_backend.web.model.response.CommonResponseDto;
import com.isedol_clip_backend.web.model.response.ResponseData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class MakeResp {
    public static ResponseEntity<CommonResponseDto> make(HttpStatus httpStatus, String msg, ResponseData data) {
        CommonResponseDto dto = new CommonResponseDto(getStatus(httpStatus), msg, data);
        return new ResponseEntity<>(dto, httpStatus);
    }

    public static ResponseEntity<CommonResponseDto> make(HttpStatus httpStatus, String msg) {
        CommonResponseDto dto = new CommonResponseDto(getStatus(httpStatus), msg);
        return new ResponseEntity<>(dto, httpStatus);
    }

    private static String getStatus(HttpStatus httpStatus) {
        return httpStatus.value() + " " + httpStatus.getReasonPhrase();
    }
}
