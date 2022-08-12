package com.isedol_clip_backend.util.aop;

import com.isedol_clip_backend.exception.*;
import com.isedol_clip_backend.util.MakeResp;
import com.isedol_clip_backend.web.model.response.CommonResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.isedol_clip_backend.web.controller")
public class GlobalExceptionHandler {

//    @ExceptionHandler(NoExistedDataException.class)
//    public ResponseEntity<CommonResponse> noExistedDataHandler() {
//        return MakeResp.make(HttpStatus.NO_CONTENT, "No Existed Data");
//    }
//
//    @ExceptionHandler(AlreadyExistedDataException.class)
//    public ResponseEntity<CommonResponse> alreadyExistedDataHandler(AlreadyExistedDataException e) {
//        return MakeResp.make(HttpStatus.CONFLICT, e.getMessage());
//    }
//
    @ExceptionHandler(ApiRequestException.class)
    public ResponseEntity<CommonResponse> apiRequestExceptionHandler(ApiRequestException e) {
        return MakeResp.make(e.getHttpStatus(), e.getMessage());
    }

//    @ExceptionHandler(ExpiredJwtException.class)
//    public ResponseEntity<CommonResponse> expiredRefreshTokenHandler() {
//        return MakeResp.make(HttpStatus.BAD_REQUEST, "Expired Refresh Token. Need relogin");
//    }
//
//    @ExceptionHandler(InvalidJwtException.class)
//    public ResponseEntity<CommonResponse> InvalidJwtException(InvalidJwtException e) {
//        return MakeResp.make(HttpStatus.BAD_REQUEST, e.getMessage());
//    }
//
//    @ExceptionHandler(InvalidParameterException.class)
//    public ResponseEntity<CommonResponse> InvalidParameterException(InvalidJwtException e) {
//        return MakeResp.make(HttpStatus.BAD_REQUEST, e.getMessage());
//    }
//
//    @ExceptionHandler(NullPointerException.class)
//    public ResponseEntity<CommonResponse> invalidRequestParams(NullPointerException e) {
//        return MakeResp.make(HttpStatus.BAD_REQUEST, e.getMessage());
//    }

    @ExceptionHandler({
            NullPointerException.class,
            InvalidParameterException.class,
            InvalidJwtException.class,
            ExpiredJwtException.class,
    })
    public ResponseEntity<CommonResponse> badRequestHandler(Exception e) {
        return MakeResp.make(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(AlreadyExistedDataException.class)
    public ResponseEntity<CommonResponse> conflictHandler(Exception e) {
        return MakeResp.make(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(NoExistedDataException.class)
    public ResponseEntity<CommonResponse> noContentHandler(Exception e) {
        return MakeResp.make(HttpStatus.NO_CONTENT, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse> internalServerErrorHandler(Exception e) {
        log.error("!!! FATAL ERROR !!! Unknown Exception");
        e.printStackTrace();
        return MakeResp.make(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
