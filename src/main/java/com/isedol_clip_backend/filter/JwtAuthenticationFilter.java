package com.isedol_clip_backend.filter;

import com.isedol_clip_backend.auth.JwtTokenProvider;
import com.isedol_clip_backend.auth.TokenState;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        log.info("URI : {}", requestUri);

        String jwt = getJwtFromCookie(request);
        log.info("jwt: {}", jwt);
        request.setAttribute("jwt", jwt);

        if(jwt == null || jwt.isEmpty()) {
            request.setAttribute("tokenState", TokenState.HASNOT);

        } else {
            try {
                JwtTokenProvider.getTokenClaims(jwt);
            } catch (ExpiredJwtException e) {
                log.info("Expired Token");
                request.setAttribute("tokenState", TokenState.EXPIRED);
                filterChain.doFilter(request, response);
                return;
            } catch (Exception e) {
                log.info("Invalid Token");
                request.setAttribute("tokenState", TokenState.INVALID);
                filterChain.doFilter(request, response);
                return;
            }
            try {
                SecurityContextHolder.getContext().setAuthentication(JwtTokenProvider.getAuthentication(jwt));
            } catch (Exception e) {
                log.error("인증 실패, 알 수 없는 에러");
                e.printStackTrace();
                filterChain.doFilter(request, response);
                return;
            }
            log.info("인증 완료");
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }

        return null;
    }

    private String getJwtFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                log.info("cookie: {} = {}", cookie.getName(), cookie.getValue());
                if(cookie.getName().equals("tk")) {
//                    log.info("tk={}", cookie.getValue());
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}


