package com.tripperr.api.trip.dto;

import com.tripperr.api.trip.model.TripType;
import com.tripperr.api.trip.model.Visibility;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public record TripPlanResponse(
        String id,
        String title,
        String description,
        String destination,
        String country,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal estimatedBudget,
        String currency,
        TripType tripType,
        String coverImage,
        Set<String> tags,
        Visibility visibility,
        String ownerId,
        Set<String> collaborators,
        List<TripDayDto> itineraryDays,
        Instant createdAt,
        Instant updatedAt
) {}
