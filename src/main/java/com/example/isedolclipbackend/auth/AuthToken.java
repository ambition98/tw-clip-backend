package com.example.isedolclipbackend.auth;

import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
public class AuthToken {

    @Getter
    private final String token;
    private final Key key;

    public AuthToken(long id, Date expiry, Key key) {
        this.key = key;
        this.token = Jwts.builder()
                .setSubject(String.valueOf(id))
    //                .claim(AUTHORITIES_KEY, role)
                .setIssuedAt(new Date())
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(expiry)
                .compact();
    }

    public String getUserId() {
        return this.getTokenClaims().getSubject();
    }

    public boolean validate() {
        return this.getTokenClaims() != null;
    }

    public Claims getTokenClaims() {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
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
}