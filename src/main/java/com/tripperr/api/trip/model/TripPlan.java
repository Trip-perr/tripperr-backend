package com.tripperr.api.trip.model;

import com.tripperr.api.common.BaseDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "trip_plans")
public class TripPlan extends BaseDocument {

    @Id
    private String id;

    private String title;
    private String description;

    @Indexed
    private String destination;

    @Indexed
    private String country;

    private LocalDate startDate;
    private LocalDate endDate;

    private BigDecimal estimatedBudget;
    private String currency;

    private TripType tripType;
    private String coverImage;

    @Builder.Default
    private Set<String> tags = new HashSet<>();

    @Builder.Default
    private Visibility visibility = Visibility.PRIVATE;

    @Indexed
    private String ownerId;

    @Builder.Default
    private Set<String> collaborators = new HashSet<>();

    @Builder.Default
    private List<TripDay> itineraryDays = new ArrayList<>();
}
