package com.guvi.newsletter_campaign_mgr.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final long expirationMs;
    private final SecretKey secretKey;

    public JwtUtil(
            @Value("${app.jwt.expiration-ms}") long expirationMs,
            @Value("${app.jwt.secret}") String secret) {
        this.expirationMs = expirationMs;
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // Generate token from username
    public String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(secretKey)
                .compact();
    }

    // Validate token — catches expired, malformed, invalid signature
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    // Extract username from token subject
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    // Parse and verify claims
    public Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
