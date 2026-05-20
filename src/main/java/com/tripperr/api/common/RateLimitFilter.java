package com.tripperr.api.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory token bucket rate limiter for sensitive auth endpoints. Designed to
 * cap brute-force attempts on a single-instance deployment; horizontal
 * deployments should replace the in-memory map with a Redis-backed bucket.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private static final int BUCKET_CAPACITY = 20;
    private static final long REFILL_INTERVAL_MS = 60_000L;
    private static final long REFILL_AMOUNT = 20L;

    private final ObjectMapper objectMapper;
    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return !(path.startsWith("/api/v1/auth/login")
                || path.startsWith("/api/v1/auth/register")
                || path.startsWith("/api/v1/auth/refresh"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String key = resolveClient(request);
        Bucket bucket = buckets.computeIfAbsent(key, k -> new Bucket(BUCKET_CAPACITY));
        if (!bucket.tryConsume()) {
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setHeader("Retry-After", "60");
            ApiResponse<Void> body = ApiResponse.fail(ApiError.of(
                    "RATE_LIMITED",
                    "Too many attempts — slow down for a moment.",
                    request.getRequestURI()));
            response.getWriter().write(objectMapper.writeValueAsString(body));
            return;
        }
        chain.doFilter(request, response);
    }

    private String resolveClient(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            int comma = forwarded.indexOf(',');
            return (comma > 0 ? forwarded.substring(0, comma) : forwarded).trim();
        }
        return request.getRemoteAddr();
    }

    private static final class Bucket {
        private final AtomicLong tokens;
        private final AtomicLong lastRefillMs;
        private final long capacity;

        Bucket(long capacity) {
            this.capacity = capacity;
            this.tokens = new AtomicLong(capacity);
            this.lastRefillMs = new AtomicLong(System.currentTimeMillis());
        }

        boolean tryConsume() {
            refill();
            while (true) {
                long current = tokens.get();
                if (current <= 0) return false;
                if (tokens.compareAndSet(current, current - 1)) return true;
            }
        }

        private void refill() {
            long now = System.currentTimeMillis();
            long last = lastRefillMs.get();
            long elapsed = now - last;
            if (elapsed < REFILL_INTERVAL_MS) return;
            if (!lastRefillMs.compareAndSet(last, now)) return;
            long increments = (elapsed / REFILL_INTERVAL_MS) * REFILL_AMOUNT;
            if (increments <= 0) return;
            tokens.updateAndGet(t -> Math.min(capacity, t + increments));
        }
    }
}
