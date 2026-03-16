package com.astrocode.backend.domain.services;

import com.astrocode.backend.domain.exceptions.InvalidTokenException;
import com.astrocode.backend.domain.model.enums.PlanType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Base64;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private static final int JWT_EXPIRATION_DAYS = 7;
    private static final String USER_ID_CLAIM = "user_id";
    private static final String EMAIL_CLAIM = "email";
    private static final String PLAN_CLAIM = "plan";
    private static final String PLAN_EXPIRES_AT_CLAIM = "plan_expires_at";

    private final SecretKey secretKey;

    public JwtService(@Value("${jwt.secret}") String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("JWT secret não pode ser vazio. Configure a variável de ambiente JWT_SECRET.");
        }
        byte[] decoded = Base64.getDecoder().decode(secret);
        if (decoded.length < 32) {
            throw new IllegalArgumentException("JWT secret deve ter pelo menos 32 bytes (Base64 decodificado). Gere com SecureRandom.");
        }
        this.secretKey = Keys.hmacShaKeyFor(decoded);
    }

    public String generateToken(UUID userId, String email, PlanType planType, OffsetDateTime planExpiresAt) {
        Instant now = Instant.now();
        Instant expiration = now.plus(JWT_EXPIRATION_DAYS, ChronoUnit.DAYS);

        var builder = Jwts.builder()
                .claim(USER_ID_CLAIM, userId.toString())
                .claim(EMAIL_CLAIM, email)
                .claim(PLAN_CLAIM, planType.name());

        if (planExpiresAt != null) {
            builder.claim(PLAN_EXPIRES_AT_CLAIM, planExpiresAt.toInstant().toEpochMilli());
        }

        return builder
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

    public String extractPlan(String token) {
        Claims claims = extractClaims(token);
        String plan = claims.get(PLAN_CLAIM, String.class);
        return plan != null ? plan : PlanType.FREE.name();
    }

    public OffsetDateTime extractPlanExpiresAt(String token) {
        Claims claims = extractClaims(token);
        Long epochMilli = claims.get(PLAN_EXPIRES_AT_CLAIM, Long.class);
        return epochMilli != null ? OffsetDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), ZoneOffset.UTC) : null;
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
