package com.tripperr.api.trip.dto;

import com.tripperr.api.trip.model.ActivityCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalTime;

public record ActivityDto(
        @NotBlank @Size(max = 120) String title,
        @Size(max = 2000) String description,
        @Size(max = 200) String location,
        LocalTime startTime,
        LocalTime endTime,
        ActivityCategory category,
        @Size(max = 1000) String notes,
        @PositiveOrZero BigDecimal estimatedCost
) {}
