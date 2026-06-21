package com.example.demo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration-ms}")
    private long expirationMs;

    // Build the cryptographic key object from the secret string
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ---- JOB 1: Mint a token for a given username ----
    public String generateToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(username)        // the "sub" claim — who this token is for
                .issuedAt(now)            // "iat" — when minted
                .expiration(expiry)       // "exp" — when it dies
                .signWith(getSigningKey()) // sign with our secret → the signature chunk
                .compact();               // serialize to the header.payload.signature string
    }

    // ---- JOB 2: Verify a token and pull the username out ----
    public String extractUsername(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())  // checks the signature against our secret
                .build()
                .parseSignedClaims(token)     // throws if signature invalid OR expired
                .getPayload();

        return claims.getSubject();
    }

    // Boolean convenience wrapper — is this token valid?
    public boolean isTokenValid(String token) {
        try {
            extractUsername(token);  // if parsing succeeds, it's valid
            return true;
        } catch (Exception e) {
            return false;            // bad signature, expired, malformed → invalid
        }
    }
}