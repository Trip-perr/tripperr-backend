package com.tripperr.api.auth;

import com.tripperr.api.auth.dto.AuthResponse;
import com.tripperr.api.auth.dto.LoginRequest;
import com.tripperr.api.auth.dto.RefreshRequest;
import com.tripperr.api.auth.dto.RegisterRequest;
import com.tripperr.api.common.ApiResponse;
import com.tripperr.api.security.SecurityContextHelper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication and token management")
public class AuthController {

    private final AuthService authService;
    private final SecurityContextHelper securityContextHelper;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest req) {
        AuthResponse res = authService.register(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(res));
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(req)));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Exchange a refresh token for a new access token")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody RefreshRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.refresh(req)));
    }

    @PostMapping("/logout")
    @Operation(summary = "Revoke all refresh tokens for the current user")
    public ResponseEntity<ApiResponse<Void>> logout() {
        authService.logout(securityContextHelper.requireUserId());
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
