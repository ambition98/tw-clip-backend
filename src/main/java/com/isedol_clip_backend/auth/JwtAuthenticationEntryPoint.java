package com.isedol_clip_backend.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.info("Exception 발생, JwtAuthenicationEntryPoint 도달");

        TokenState state = (TokenState) request.getAttribute("tokenState");
        switch (state) {
            case INVALID:
            case HASNOT:
                log.info("Need Authorized");
                response.sendError(400, "Need Authorized");
                return;
            case EXPIRED:
                log.info("Exired access token");
                response.sendError(401, "Exired access token");
        }
    }
}
