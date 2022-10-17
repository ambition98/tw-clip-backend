package com.isedol_clip_backend.filter;

import com.isedol_clip_backend.auth.JwtTokenProvider;
import com.isedol_clip_backend.util.CookieUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
//@Component
//@Order(2)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String jwt = CookieUtil.getCookie(request, "tk");
//        log.info("jwt: {}", jwt);
//        request.setAttribute("jwt", jwt);

        if(jwt != null && !jwt.isEmpty()) {
            try {
                JwtTokenProvider.getTokenClaims(jwt);
            } catch (Exception e) {
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
//            log.info("인증 완료");
        }

        filterChain.doFilter(request, response);
    }
}


