package com.example.isedolclipbackend.web.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor
public class CommonResponseDto {
    private HttpStatus status;
    private String msg;
    private String data;

    public CommonResponseDto(HttpStatus status, String msg) {
        this.status = status;
        this.msg = msg;
    }
}
