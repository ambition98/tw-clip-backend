package com.isedol_clip_backend.filter;

import com.isedol_clip_backend.auth.JwtTokenProvider;
import com.isedol_clip_backend.util.UserAuthentication;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@WebFilter(urlPatterns = "/oauth")
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

//        String requestUri = request.getRequestURI();
//        log.info("URI : {}", requestUri);
//
//        if(!requestUri.equals("/isedol-clip/oauth")) {
//            filterChain.doFilter(request, response);
//            return;
//        }

        try {
//            String jwt = getJwtFromRequest(request);
            String jwt = getJwtFromCookie(request);
            log.info("JWT: "+jwt);

            if (jwt != null && !jwt.isEmpty() && JwtTokenProvider.isValidToken(jwt)) {
                log.info("JwtauthenticationFilter 진입");

                String userId = JwtTokenProvider.getId(jwt); //jwt에서 사용자 id를 꺼낸다.
                log.info("userId in filter: {}", userId);

                UserAuthentication authentication = new UserAuthentication(userId, null, null); //id를 인증한다.
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); //기본적으로 제공한 details 세팅

                SecurityContextHolder.getContext().setAuthentication(authentication); //세션에서 계속 사용하기 위해 securityContext에 Authentication 등록
            } else {
                // 추후 수정해야댐. 인증가능한 링크안내
                response.setHeader("WWW-Authenticate", "Basic realm=\"/isedol-clip/\"");
                //토큰이 비어있지 않으나 잘못된 토큰
//                if (jwt != null && !jwt.isEmpty()) {
//                    log.error("401 인증키 없음.");
//                    request.setAttribute("unauthorization", "401 인증키 없음.");
//                }

//                if (JwtTokenProvider.isValidJwtToken(jwt)) {
//                    log.error("401-001 인증키 만료.");
//                    request.setAttribute("unauthorization", "401-001 인증키 만료.");
//                }
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }

        return null;
    }

    private String getJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies == null) {
            log.info("Not exist cookies in request");

        } else {
            for(Cookie cookie : cookies) {
                log.info("cookie: {} = {}", cookie.getName(), cookie.getValue());
                if(cookie.getName().equals("tk")) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}


