package com.pulse.security;

import com.pulse.entity.dto.auth.JWTAuthentificationDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtCore {
    private final String secret;
    private final int authLifetime, refreshLifetime;

    public JwtCore(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.access-token-expiration-minutes}") int authLifetime,
                   @Value("${jwt.refresh-token-expiration-days}")int refreshLifetime) {
        this.secret = secret;
        this.authLifetime = authLifetime;
        this.refreshLifetime = refreshLifetime;
    }

    public JWTAuthentificationDto createAuthToken(String email) {
        return new JWTAuthentificationDto(generateAuthToken(email), generateRefreshToken(email));
    }

    public JWTAuthentificationDto createAuthToken(String email, String refreshToken) {
        return new JWTAuthentificationDto(generateAuthToken(email), refreshToken);
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    public boolean validateJwtToken(String token) {
        try {
            Jwts
                    .parser()
                    .verifyWith(getSignKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return true;
        } catch(Exception e) {
            return false;
        }
    }

    private String generateAuthToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusMinutes(authLifetime).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts
                .builder()
                .subject(email)
                .expiration(date)
                .signWith(getSignKey())
                .compact();
    }

    private String generateRefreshToken(String email) {
        Date date = Date.from(LocalDateTime.now().plusDays(refreshLifetime).atZone(ZoneId.systemDefault()).toInstant());
        return Jwts
                .builder()
                .subject(email)
                .expiration(date)
                .signWith(getSignKey())
                .compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
