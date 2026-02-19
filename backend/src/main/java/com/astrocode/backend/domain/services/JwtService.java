package com.astrocode.backend.domain.services;

import com.astrocode.backend.domain.exceptions.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private static final int JWT_EXPIRATION_DAYS = 14;
    private static final String USER_ID_CLAIM = "user_id";
    private static final String EMAIL_CLAIM = "email";

    private final SecretKey secretKey;

    public JwtService(@Value("${jwt.secret}") String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("JWT secret não pode ser vazio. Configure a variável de ambiente JWT_SECRET.");
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UUID userId, String email) {
        Instant now = Instant.now();
        Instant expiration = now.plus(JWT_EXPIRATION_DAYS, ChronoUnit.DAYS);

        return Jwts.builder()
                .claim(USER_ID_CLAIM, userId.toString())
                .claim(EMAIL_CLAIM, email)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public Claims extractClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new InvalidTokenException("Token inválido: " + e.getMessage(), e);
        }
    }

    public UUID extractUserId(String token) {
        Claims claims = extractClaims(token);
        String userIdStr = claims.get(USER_ID_CLAIM, String.class);
        if (userIdStr == null) {
            throw new InvalidTokenException("Token não contém user_id");
        }
        try {
            return UUID.fromString(userIdStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException("user_id inválido no token", e);
        }
    }

    public String extractEmail(String token) {
        Claims claims = extractClaims(token);
        String email = claims.get(EMAIL_CLAIM, String.class);
        if (email == null) {
            throw new InvalidTokenException("Token não contém email");
        }
        return email;
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (InvalidTokenException e) {
            return false;
        }
    }
}
