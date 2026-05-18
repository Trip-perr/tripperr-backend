package com.tripperr.api.auth.dto;

import com.tripperr.api.user.dto.UserResponse;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresInSeconds,
        UserResponse user
) {
    public static AuthResponse of(String access, String refresh, long ttlSec, UserResponse user) {
        return new AuthResponse(access, refresh, "Bearer", ttlSec, user);
    }
}
