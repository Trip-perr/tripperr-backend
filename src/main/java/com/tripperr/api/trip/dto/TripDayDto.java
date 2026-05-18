package com.tripperr.api.trip.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record TripDayDto(
        @Min(1) int dayNumber,
        @NotNull LocalDate date,
        @Valid List<ActivityDto> activities
) {}
