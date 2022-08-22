package com.isedol_clip_backend.auth;

import com.isedol_clip_backend.util.CookieUtil;
import io.jsonwebtoken.ExpiredJwtException;
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

        String jwt = CookieUtil.getCookie(request, "tk");
        PrintWriter out = response.getWriter();
        response.setHeader("Content-Type", "application/json");
        String responseJson = "";
        log.info("method: {}", request.getMethod());

        try {
            JwtTokenProvider.getTokenClaims(jwt);
        } catch (ExpiredJwtException e) {
                response.setStatus(401);
                log.info("Expired access token");
                responseJson = makeResponseJson(HttpStatus.UNAUTHORIZED, "Exired access token");
        } catch (Exception e) {
                response.setStatus(400);
                log.info("Need Authorized");
                responseJson = makeResponseJson(HttpStatus.BAD_REQUEST, "Need Login");
        }

        out.print(responseJson);
        out.flush();
    }

    private String makeResponseJson(HttpStatus httpStatus, String msg) {
        return "{httpstatus: " + httpStatus + ", msg: " + msg + "}";
    }
}
