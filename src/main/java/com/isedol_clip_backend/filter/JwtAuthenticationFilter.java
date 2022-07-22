package com.isedol_clip_backend.filter;

import com.isedol_clip_backend.auth.JwtTokenProvider;
import com.isedol_clip_backend.auth.UserAuthentication;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestUri = request.getRequestURI();
        log.info("URI : {}", requestUri);

//        String jwt = getJwtFromCookie(request);
        String jwt = getJwtFromRequest(request);
//        log.info("JWT: "+jwt);

        request.setAttribute("jwt", jwt);
        if (jwt != null && !jwt.isEmpty()) {

            Claims claims = null;
            try {
                claims = JwtTokenProvider.getTokenClaims(jwt);
            } catch (Exception e) {
                filterChain.doFilter(request, response);
                return;
            }
            String userId = claims.getId();

            String role = claims.get("role", String.class);
//            log.info("UserId in filter: {}", userId);

            List<GrantedAuthority> grantList = new ArrayList<>();
            grantList.add(new SimpleGrantedAuthority(role));

            UserAuthentication authentication
                    = new UserAuthentication(userId, null, grantList);
            authentication.setDetails(new WebAuthenticationDetailsSource()
                    .buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("인증 완료");
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

//    private String getJwtFromCookie(HttpServletRequest request) {
//        Cookie[] cookies = request.getCookies();
//        if(cookies != null) {
//            for(Cookie cookie : cookies) {
////                log.info("cookie: {} = {}", cookie.getName(), cookie.getValue());
//                if(cookie.getName().equals("tk")) {
//                    return cookie.getValue();
//                }
//            }
//        }
//
//        return null;
//    }
}


