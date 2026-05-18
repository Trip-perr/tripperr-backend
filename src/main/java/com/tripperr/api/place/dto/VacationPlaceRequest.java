package com.tripperr.api.place.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public record VacationPlaceRequest(
        @NotBlank @Size(max = 160) String name,
        @Size(max = 120) String city,
        @NotBlank @Size(max = 120) String country,
        @Size(max = 4000) String description,
        Set<@Size(max = 40) String> categories,
        @DecimalMin("0.0") @DecimalMax("5.0") Double rating,
        List<@Size(max = 500) String> images,
        @Size(max = 200) String bestTimeToVisit,
        @PositiveOrZero BigDecimal estimatedBudget,
        @Size(max = 3) String currency,
        Set<@Size(max = 40) String> tags,
        @Valid CoordinatesDto coordinates
) {}
