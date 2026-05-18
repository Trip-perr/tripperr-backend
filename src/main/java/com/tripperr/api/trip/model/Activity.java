package com.tripperr.api.trip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Activity {
    private String title;
    private String description;
    private String location;
    private LocalTime startTime;
    private LocalTime endTime;
    private ActivityCategory category;
    private String notes;
    private BigDecimal estimatedCost;
}
