package com.tripperr.api.trip.dto;

import com.tripperr.api.trip.model.TripType;
import com.tripperr.api.trip.model.Visibility;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record TripPlanRequest(
        @NotBlank @Size(max = 160) String title,
        @Size(max = 4000) String description,
        @NotBlank @Size(max = 120) String destination,
        @Size(max = 120) String country,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate,
        @PositiveOrZero BigDecimal estimatedBudget,
        @Size(max = 3) String currency,
        TripType tripType,
        @Size(max = 500) String coverImage,
        Set<@Size(max = 40) String> tags,
        Visibility visibility,
        Set<String> collaborators,
        @Valid List<TripDayDto> itineraryDays
) {}
