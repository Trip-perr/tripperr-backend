package com.tripperr.api.trip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripDay {
    private int dayNumber;
    private LocalDate date;

    @Builder.Default
    private List<Activity> activities = new ArrayList<>();
}
