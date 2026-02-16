package com.example.demo.security;

import com.example.demo.entity.dto.JWTAuthentificationDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JWTCore {
    @Value("${jwt.secret}")
    private String secret;

    private int authLifetimeMs = 60 * 1000;
    private int refreshLifetimeMs = 60 * 1000 * 60 * 24;

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

    public String generateAuthToken(String email) {
        return Jwts
                .builder()
                .subject(email)
                .expiration(new Date(System.currentTimeMillis() + authLifetimeMs))
                .signWith(getSignKey())
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts
                .builder()
                .subject(email)
                .expiration(new Date(System.currentTimeMillis() + refreshLifetimeMs))
                .signWith(getSignKey())
                .compact();
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
