package com.tripperr.api.security;

import com.tripperr.api.user.model.Role;
import com.tripperr.api.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class JwtService {

    private static final String CLAIM_TYPE = "typ";
    private static final String CLAIM_ROLES = "roles";

    private final JwtProperties props;
    private final SecretKey signingKey;

    public JwtService(JwtProperties props) {
        this.props = props;
        byte[] keyBytes = props.secret().getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalStateException(
                    "JWT secret must be at least 32 bytes (256 bits). Configure app.security.jwt.secret.");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(User user) {
        return buildToken(user, TokenType.ACCESS, props.accessTokenTtlMinutes(), ChronoUnit.MINUTES);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, TokenType.REFRESH, props.refreshTokenTtlDays(), ChronoUnit.DAYS);
    }

    public long getAccessTokenTtlSeconds() {
        return props.accessTokenTtlMinutes() * 60;
    }

    public long getRefreshTokenTtlSeconds() {
        return props.refreshTokenTtlDays() * 24 * 60 * 60;
    }

    private String buildToken(User user, TokenType type, long amount, ChronoUnit unit) {
        Instant now = Instant.now();
        Instant expiry = now.plus(amount, unit);

        List<String> roleNames = user.getRoles().stream().map(Role::name).toList();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId())
                .issuer(props.issuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .claim(CLAIM_TYPE, type.name())
                .claim(CLAIM_ROLES, roleNames)
                .claim("email", user.getEmail())
                .signWith(signingKey, Jwts.SIG.HS256)
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .requireIssuer(props.issuer())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isValid(String token, TokenType expected) {
        try {
            Claims c = parse(token);
            String typ = c.get(CLAIM_TYPE, String.class);
            return expected.name().equals(typ);
        } catch (JwtException | IllegalArgumentException ex) {
            log.debug("JWT validation failed: {}", ex.getMessage());
            return false;
        }
    }

    public String extractUserId(String token) {
        return parse(token).getSubject();
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Object roles = parse(token).get(CLAIM_ROLES);
        return roles instanceof List<?> list ? (List<String>) list : List.of();
    }
}
