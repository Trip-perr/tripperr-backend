package com.tripperr.api.security;

import com.tripperr.api.user.model.Role;
import com.tripperr.api.user.model.User;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final JwtProperties props = new JwtProperties(
            "test-secret-with-at-least-thirty-two-characters!",
            15,
            30,
            "tripperr-api-test"
    );
    private final JwtService jwtService = new JwtService(props);

    private User sampleUser() {
        return User.builder()
                .id("user-123")
                .email("a@b.com")
                .name("Test")
                .passwordHash("hash")
                .roles(Set.of(Role.USER))
                .enabled(true)
                .build();
    }

    @Test
    void generatesValidAccessToken() {
        String token = jwtService.generateAccessToken(sampleUser());
        assertTrue(jwtService.isValid(token, TokenType.ACCESS));
        assertEquals("user-123", jwtService.extractUserId(token));
        assertTrue(jwtService.extractRoles(token).contains(Role.USER.name()));
    }

    @Test
    void accessTokenIsNotValidAsRefresh() {
        String token = jwtService.generateAccessToken(sampleUser());
        assertFalse(jwtService.isValid(token, TokenType.REFRESH));
    }

    @Test
    void rejectsShortSecret() {
        JwtProperties bad = new JwtProperties("too-short", 15, 30, "iss");
        assertThrows(IllegalStateException.class, () -> new JwtService(bad));
    }
}
