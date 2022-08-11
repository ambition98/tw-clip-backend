package com.isedol_clip_backend.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        TokenState state = (TokenState) request.getAttribute("tokenState");
        PrintWriter out = response.getWriter();
        response.setHeader("Content-Type", "application/json");
        String responseJson = "";

        switch (state) {
            case INVALID:
            case HASNOT:
                response.setStatus(400);
                log.info("Need Authorized");
                responseJson = makeResponseJson(HttpStatus.BAD_REQUEST, "Need Login");
//                response.sendError(400, "Need Authorized");
                break;
            case EXPIRED:
                response.setStatus(401);
                log.info("Expired access token");
                responseJson = makeResponseJson(HttpStatus.UNAUTHORIZED, "Exired access token");
//                response.sendError(401, "Exired access token");
        }

        out.print(responseJson);
        out.flush();
    }

    private String makeResponseJson(HttpStatus httpStatus, String msg) {
        return "{httpstatus: " + httpStatus + ", msg: " + msg + "}";
    }
}
