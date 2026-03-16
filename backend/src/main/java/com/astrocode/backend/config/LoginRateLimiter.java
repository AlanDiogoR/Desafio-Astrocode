package com.astrocode.backend.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class LoginRateLimiter {

    private static final long TTL_MS = 2 * 60 * 60 * 1000;

    private record CacheEntry(Bucket bucket, long expiresAt) {}

    private final Map<String, CacheEntry> buckets = new ConcurrentHashMap<>();

    public Bucket getBucketForIp(String ip) {
        evictExpired();
        return buckets.compute(ip, (k, existing) -> {
            long now = System.currentTimeMillis();
            if (existing != null && existing.expiresAt > now) {
                return existing;
            }
            Bucket bucket = Bucket.builder()
                    .addLimit(Bandwidth.simple(10, Duration.ofMinutes(1)))
                    .addLimit(Bandwidth.simple(50, Duration.ofHours(1)))
                    .build();
            return new CacheEntry(bucket, now + TTL_MS);
        }).bucket();
    }

    private void evictExpired() {
        long now = System.currentTimeMillis();
        buckets.entrySet().removeIf(e -> e.getValue().expiresAt < now);
    }
}
