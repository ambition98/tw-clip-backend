package com.isedol_clip_backend.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.isedol_clip_backend.util.LoadSecret;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private static final Key KEY;
    private static final long ACCESS_TOKEN_EXPIRY_MS = 1800000; //30분
    private static final long REFRESH_TOKEN_EXPIRY_MS = 259200000; //3일
    private static final String AUTHORITIES_KEY = "role";

    static {
        KEY = Keys.hmacShaKeyFor(LoadSecret.jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public static String generateUserToken(long id) {
        return generateToken(id, "Access Token", UserRole.USER, ACCESS_TOKEN_EXPIRY_MS);
    }

    public static String generateAdminToken(long id) {
        return generateToken(id, "Access Token", UserRole.ADMIN, ACCESS_TOKEN_EXPIRY_MS);
    }

    public static String generateRefreshToken(long id) {
        return generateToken(id, "Refresh Token", UserRole.USER, REFRESH_TOKEN_EXPIRY_MS);
    }

    private static String generateToken(long id, String subject, UserRole role, long expiryMs) {
        Date currentTime = new Date();

        return Jwts.builder()
                .setSubject(subject)
                .setId(String.valueOf(id))
                .setIssuedAt(new Date())
                .signWith(KEY, SignatureAlgorithm.HS256)
                .setExpiration(new Date(currentTime.getTime() + expiryMs))
                .claim("role", role)
                .compact();
    }

    //!!주의: 해당 메서드는 토큰 유효성 검사를 하지 않음
    public static String getIdWithoutValidate(String token) {
        StringTokenizer st = new StringTokenizer(token, ".");
        st.nextToken();
        Base64.Decoder decoder = Base64.getDecoder();
        String payload = new String(decoder.decode(st.nextToken()));

        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = null;
        try {
            map = mapper.readValue(payload, Map.class);
        } catch (JsonProcessingException e) {
            return null;
        }

        return map.get("jti");
    }

    public static Claims getTokenClaims(String token) throws Exception {
//        try {
            return Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

//        } catch (SecurityException e) {
//            log.info("Invalid JWT signature.");
//        } catch (MalformedJwtException e) {
//            log.info("Invalid JWT token.");
//        } catch (ExpiredJwtException e) {
//            log.info("Expired JWT token.");
//        } catch (UnsupportedJwtException e) {
//            log.info("Unsupported JWT token.");
//        } catch (IllegalArgumentException e) {
//            log.info("JWT token compact of handler are invalid.");
//        } catch (io.jsonwebtoken.security.SignatureException e) {
//            log.info("Invalid JWT token signature");
//        }
//
//        return null;
    }

    public static boolean isValidToken(String token) {
        try {
            getTokenClaims(token);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static Authentication getAuthentication(String token) throws Exception {
        Claims claims = getTokenClaims(token);
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(new String[]{claims.get(AUTHORITIES_KEY).toString()})
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
}