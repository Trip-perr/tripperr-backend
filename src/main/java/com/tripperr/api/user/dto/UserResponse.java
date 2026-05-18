package com.tripperr.api.user.dto;

import com.tripperr.api.user.model.Role;
import com.tripperr.api.user.model.User;

import java.time.Instant;
import java.util.Set;

public record UserResponse(
        String id,
        String name,
        String email,
        Set<Role> roles,
        boolean enabled,
        String profileImageUrl,
        Instant createdAt,
        Instant updatedAt
) {
    public static UserResponse from(User u) {
        return new UserResponse(
                u.getId(),
                u.getName(),
                u.getEmail(),
                u.getRoles(),
                u.isEnabled(),
                u.getProfileImageUrl(),
                u.getCreatedAt(),
                u.getUpdatedAt()
        );
    }
}
