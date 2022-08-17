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
        Cookie cookie = makeCookie(token);
        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletResponse response) {
        Cookie cookie = makeCookie("deleted");
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
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setDomain("isedol-clip.xyz");

        return cookie;
    }
}
