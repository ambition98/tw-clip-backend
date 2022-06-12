package com.isedol_clip_backend.auth;

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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private static final Key KEY;
    private static final int EXPIRY_MS = 1800000;
    private static final String AUTHORITIES_KEY = "role";

    static {
        KEY = Keys.hmacShaKeyFor(LoadSecret.jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
//    public JwtTokenProvider() {
//        byte[] keyBytes = Base64.getDecoder().decode(LoadSecret.jwtSecret);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
//        this.key = Keys.hmacShaKeyFor(LoadSecret.jwtSecret.getBytes(StandardCharsets.UTF_8));
//    }
//
//    public JwtToken generateToken(long id) {
//        Date expiry = new Date(new Date().getTime() + jwtExpirationMs);
//
//        return new JwtToken(id, expiry, key);
//    }

    public static String generateToken(long id) {
        Date currentTime = new Date();

        return Jwts.builder()
                .setSubject("Twitch login")
                .setId(String.valueOf(id))
                .setIssuedAt(new Date())
                .signWith(KEY, SignatureAlgorithm.HS256)
                .setExpiration(new Date(currentTime.getTime() + EXPIRY_MS))
                .compact();
//                .claim(AUTHORITIES_KEY, role)
    }

    public static String getId(String token) {
        Claims claims = getTokenClaims(token);

        if(claims == null)
            return null;

        return claims.getId();
    }

    public static Claims getTokenClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return null;
    }

    public static boolean isValidToken(String token) {
        return getTokenClaims(token) != null;
    }

    public static Authentication getAuthentication(String token) {

        if(isValidToken(token)) {

            Claims claims = getTokenClaims(token);
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(new String[]{claims.get(AUTHORITIES_KEY).toString()})
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            User principal = new User(claims.getSubject(), "", authorities);

            return new UsernamePasswordAuthenticationToken(principal, token, authorities);
        } else {
            log.error("Invalid Token Exception");
//            throw new InvalidTokenException();
            return null;
        }

    }
}

