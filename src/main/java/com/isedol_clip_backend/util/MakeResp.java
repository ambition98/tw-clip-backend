package com.isedol_clip_backend.util;

import com.isedol_clip_backend.web.model.response.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class MakeResp {

    public static ResponseEntity<CommonResponse> make(HttpStatus httpStatus, String msg, Object dto) {
        CommonResponse response = new CommonResponse(getStatus(httpStatus), msg, dto);
        return new ResponseEntity<>(response, httpStatus);
    }

    public static ResponseEntity<CommonResponse> make(HttpStatus httpStatus, String msg) {
        CommonResponse response = new CommonResponse(getStatus(httpStatus), msg);
        return new ResponseEntity<>(response, httpStatus);
    }

    private static String getStatus(HttpStatus httpStatus) {
        return httpStatus.value() + " " + httpStatus.getReasonPhrase();
    }
}
