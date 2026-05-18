package com.tripperr.api.place;

import com.tripperr.api.common.ApiResponse;
import com.tripperr.api.common.PageResponse;
import com.tripperr.api.place.dto.VacationPlaceRequest;
import com.tripperr.api.place.dto.VacationPlaceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/places")
@RequiredArgsConstructor
@Tag(name = "Places", description = "Vacation places / discovery")
public class VacationPlaceController {

    private final VacationPlaceService vacationPlaceService;

    @GetMapping
    @Operation(summary = "List vacation places (public)")
    public ResponseEntity<ApiResponse<PageResponse<VacationPlaceResponse>>> list(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String city,
            @PageableDefault(size = 20, sort = "rating", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(vacationPlaceService.list(country, city, pageable)));
    }

    @GetMapping("/search")
    @Operation(summary = "Search vacation places by free text and minimum rating (public)")
    public ResponseEntity<ApiResponse<PageResponse<VacationPlaceResponse>>> search(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false, defaultValue = "0") double minRating,
            @PageableDefault(size = 20, sort = "rating", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.ok(vacationPlaceService.search(query, minRating, pageable)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a vacation place by id (public)")
    public ResponseEntity<ApiResponse<VacationPlaceResponse>> get(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok(vacationPlaceService.get(id)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a vacation place (admin)")
    public ResponseEntity<ApiResponse<VacationPlaceResponse>> create(@Valid @RequestBody VacationPlaceRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok(vacationPlaceService.create(req)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update a vacation place (admin)")
    public ResponseEntity<ApiResponse<VacationPlaceResponse>> update(@PathVariable String id,
                                                                     @Valid @RequestBody VacationPlaceRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(vacationPlaceService.update(id, req)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a vacation place (admin)")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        vacationPlaceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
