package com.tripperr.api.trip;

import com.tripperr.api.common.ApiResponse;
import com.tripperr.api.common.PageResponse;
import com.tripperr.api.security.SecurityContextHelper;
import com.tripperr.api.trip.dto.TripPlanRequest;
import com.tripperr.api.trip.dto.TripPlanResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
@Tag(name = "Trips", description = "Trip plans and itineraries")
public class TripPlanController {

    private final TripPlanService tripPlanService;
    private final SecurityContextHelper security;

    @PostMapping
    @Operation(summary = "Create a trip plan")
    public ResponseEntity<ApiResponse<TripPlanResponse>> create(@Valid @RequestBody TripPlanRequest req) {
        String userId = security.requireUserId();
        TripPlanResponse res = tripPlanService.create(userId, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(res));
    }

    @GetMapping
    @Operation(summary = "List trip plans accessible to the current user (owned, collaborator, or public)")
    public ResponseEntity<ApiResponse<PageResponse<TripPlanResponse>>> list(
            @RequestParam(required = false) String query,
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String userId = security.requireUserId();
        return ResponseEntity.ok(ApiResponse.ok(tripPlanService.listAccessible(userId, query, pageable)));
    }

    @GetMapping("/mine")
    @Operation(summary = "List trip plans owned by the current user")
    public ResponseEntity<ApiResponse<PageResponse<TripPlanResponse>>> listMine(
            @PageableDefault(size = 20, sort = "updatedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        String userId = security.requireUserId();
        return ResponseEntity.ok(ApiResponse.ok(tripPlanService.listMine(userId, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a trip plan by id")
    public ResponseEntity<ApiResponse<TripPlanResponse>> get(@PathVariable String id) {
        String userId = security.requireUserId();
        return ResponseEntity.ok(ApiResponse.ok(tripPlanService.get(userId, id)));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a trip plan")
    public ResponseEntity<ApiResponse<TripPlanResponse>> update(@PathVariable String id,
                                                                @Valid @RequestBody TripPlanRequest req) {
        String userId = security.requireUserId();
        return ResponseEntity.ok(ApiResponse.ok(tripPlanService.update(userId, id, req)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a trip plan (owner only)")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        String userId = security.requireUserId();
        tripPlanService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}
