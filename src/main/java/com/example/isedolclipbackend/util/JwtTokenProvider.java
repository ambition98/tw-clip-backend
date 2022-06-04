package com.example.isedolclipbackend.util;

import com.example.isedolclipbackend.auth.AuthToken;
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
    private final Key key;
    private final int jwtExpirationMs = 1800000;
    private static final String AUTHORITIES_KEY = "role";

    public JwtTokenProvider() {
//        byte[] keyBytes = Base64.getDecoder().decode(LoadSecret.jwtSecret);
//        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.key = Keys.hmacShaKeyFor(LoadSecret.jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public AuthToken generateToken(long id) {
        Date expiry = new Date(new Date().getTime() + jwtExpirationMs);

        return new AuthToken(id, expiry, key);
    }

    public static String getUserIdFromJWT(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(LoadSecret.jwtSecret)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    // Jwt 토큰 유효성 검사
    public static boolean isValidJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(LoadSecret.jwtSecret).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }

        return false;
    }

    public Authentication getAuthentication(AuthToken authToken) {

        if(authToken.validate()) {

            Claims claims = authToken.getTokenClaims();
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(new String[]{claims.get(AUTHORITIES_KEY).toString()})
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            User principal = new User(claims.getSubject(), "", authorities);

            return new UsernamePasswordAuthenticationToken(principal, authToken, authorities);
        } else {
            log.error("Invalid Token Exception");
//            throw new InvalidTokenException();
            return null;
        }

    }
}

