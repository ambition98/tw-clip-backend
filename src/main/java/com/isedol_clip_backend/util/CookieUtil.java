package com.isedol_clip_backend.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class CookieUtil {
    public static void setCookie(HttpServletResponse response, String token) {
//        CookieGenerator cg = new CookieGenerator();
//        cg.setCookieName("tk");
//        cg.setCookieHttpOnly(true);
//        cg.setCookieSecure(true);
//        cg.addCookie(response, token);

        Cookie cookie = makeCookie(token);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletResponse response) {
//        CookieGenerator cg = new CookieGenerator();
//        cg.setCookieName("tk");
//        cg.setCookieHttpOnly(true);
//        cg.setCookieSecure(true);
//        cg.addCookie(response, "");

        Cookie cookie = makeCookie("");
        response.addCookie(cookie);
    }

    public static String getCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if(cookies != null) {
            for(Cookie cookie : cookies) {
                if(cookie.getName().equals(key)) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }

    private static Cookie makeCookie(String value) {
        Cookie cookie = new Cookie("tk", value);
        cookie.setPath("/");
        cookie.setSecure(true);
        cookie.setHttpOnly(true);

        return cookie;
    }
}
