package com.tripperr.api.place.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;

public record VacationPlaceResponse(
        String id,
        String name,
        String city,
        String country,
        String description,
        Set<String> categories,
        Double rating,
        List<String> images,
        String bestTimeToVisit,
        BigDecimal estimatedBudget,
        String currency,
        Set<String> tags,
        CoordinatesDto coordinates,
        Instant createdAt,
        Instant updatedAt
) {}
