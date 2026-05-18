package com.tripperr.api.auth;

import com.tripperr.api.auth.dto.AuthResponse;
import com.tripperr.api.auth.dto.LoginRequest;
import com.tripperr.api.auth.dto.RefreshRequest;
import com.tripperr.api.auth.dto.RegisterRequest;
import com.tripperr.api.auth.model.RefreshToken;
import com.tripperr.api.auth.repository.RefreshTokenRepository;
import com.tripperr.api.exception.BadRequestException;
import com.tripperr.api.exception.ConflictException;
import com.tripperr.api.security.JwtService;
import com.tripperr.api.security.TokenType;
import com.tripperr.api.user.dto.UserResponse;
import com.tripperr.api.user.model.Role;
import com.tripperr.api.user.model.User;
import com.tripperr.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ConflictException("Email already registered");
        }
        User user = User.builder()
                .name(req.name().trim())
                .email(email)
                .passwordHash(passwordEncoder.encode(req.password()))
                .roles(new HashSet<>(Set.of(Role.USER)))
                .enabled(true)
                .build();
        user = userRepository.save(user);
        return issueTokens(user);
    }

    public AuthResponse login(LoginRequest req) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.email().toLowerCase(), req.password()));
        } catch (BadCredentialsException ex) {
            throw new BadCredentialsException("Invalid email or password");
        }
        User user = userRepository.findByEmailIgnoreCase(req.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));
        return issueTokens(user);
    }

    @Transactional
    public AuthResponse refresh(RefreshRequest req) {
        if (!jwtService.isValid(req.refreshToken(), TokenType.REFRESH)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }
        String hash = sha256(req.refreshToken());
        RefreshToken stored = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new BadRequestException("Refresh token not recognized"));
        if (stored.isRevoked() || stored.getExpiresAt().isBefore(Instant.now())) {
            throw new BadRequestException("Refresh token expired or revoked");
        }
        User user = userRepository.findById(stored.getUserId())
                .orElseThrow(() -> new BadRequestException("User no longer exists"));

        stored.setRevoked(true);
        refreshTokenRepository.save(stored);

        return issueTokens(user);
    }

    @Transactional
    public void logout(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }

    private AuthResponse issueTokens(User user) {
        String access = jwtService.generateAccessToken(user);
        String refresh = jwtService.generateRefreshToken(user);

        RefreshToken record = RefreshToken.builder()
                .tokenHash(sha256(refresh))
                .userId(user.getId())
                .expiresAt(Instant.now().plusSeconds(jwtService.getRefreshTokenTtlSeconds()))
                .revoked(false)
                .build();
        refreshTokenRepository.save(record);

        return AuthResponse.of(access, refresh, jwtService.getAccessTokenTtlSeconds(), UserResponse.from(user));
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(input.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
