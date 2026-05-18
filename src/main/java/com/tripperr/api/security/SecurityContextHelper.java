package com.tripperr.api.security;

import com.tripperr.api.exception.ForbiddenException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SecurityContextHelper {

    public Optional<AppUserPrincipal> currentPrincipal() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
        return auth.getPrincipal() instanceof AppUserPrincipal p ? Optional.of(p) : Optional.empty();
    }

    public String requireUserId() {
        return currentPrincipal()
                .map(AppUserPrincipal::getId)
                .orElseThrow(() -> new ForbiddenException("Authentication required"));
    }
}
