package com.tripperr.api.user;

import com.tripperr.api.common.ApiResponse;
import com.tripperr.api.exception.ResourceNotFoundException;
import com.tripperr.api.security.SecurityContextHelper;
import com.tripperr.api.user.dto.UserResponse;
import com.tripperr.api.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile")
public class UserController {

    private final UserRepository userRepository;
    private final SecurityContextHelper security;

    @GetMapping("/me")
    @Operation(summary = "Get the currently authenticated user")
    public ResponseEntity<ApiResponse<UserResponse>> me() {
        String id = security.requireUserId();
        UserResponse user = userRepository.findById(id)
                .map(UserResponse::from)
                .orElseThrow(() -> ResourceNotFoundException.of("User", id));
        return ResponseEntity.ok(ApiResponse.ok(user));
    }
}
