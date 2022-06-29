package com.isedol_clip_backend.auth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        // 인가되지 않은 URI 접근 시 이 엔트리 포인트에 도달
        log.error("Responding with unauthorized error. Message - {}", authException.getMessage());
        log.info("Exception 발생. JwtAuthenicationEntryPoint 도달");

        String referer = request.getHeader("Referer");
        response.sendRedirect(referer);
        response.setStatus(401);

//        Errorcode unAuthorizationCode = (ErrorCode) request.getAttribute("unauthorization.code");
//
//        request.setAttribute("response.failure.code", unAuthorizationCode.name());
//        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, unAuthorizationCode.message());

    }
}
