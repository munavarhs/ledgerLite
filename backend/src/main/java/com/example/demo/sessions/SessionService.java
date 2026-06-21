package com.example.demo;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Service
public class SessionService {

    private final StringRedisTemplate redis;

    // Sessions expire after 1 hour — matches the JWT expiration
    private static final Duration SESSION_TTL = Duration.ofHours(1);
    private static final String SESSION_PREFIX = "session:";

    public SessionService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    // Create a session record on login. Returns the session ID.
    public String createSession(String username) {
        String sessionId = UUID.randomUUID().toString();
        String key = SESSION_PREFIX + sessionId;

        // Store the username as the value, with a TTL
        redis.opsForValue().set(key, username, SESSION_TTL);
        return sessionId;
    }

    // Check if a session still exists (not expired, not revoked)
    public boolean isSessionValid(String sessionId) {
        return Boolean.TRUE.equals(
            redis.hasKey(SESSION_PREFIX + sessionId));
    }

    // Revoke a session (logout) — deletes the record
    public void revokeSession(String sessionId) {
        redis.delete(SESSION_PREFIX + sessionId);
    }

    // Count active sessions — proves the "100 concurrent sessions" claim
    public long activeSessionCount() {
        Set<String> keys = redis.keys(SESSION_PREFIX + "*");
        return keys == null ? 0 : keys.size();
    }
}