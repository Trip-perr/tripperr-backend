package com.tripperr.api.place.model;

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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "vacation_places")
public class VacationPlace extends BaseDocument {

    @Id
    private String id;

    @Indexed
    private String name;

    @Indexed
    private String city;

    @Indexed
    private String country;

    private String description;

    @Builder.Default
    private Set<String> categories = new HashSet<>();

    private Double rating;

    private List<String> images;

    private String bestTimeToVisit;

    private BigDecimal estimatedBudget;
    private String currency;

    @Builder.Default
    private Set<String> tags = new HashSet<>();

    private Coordinates coordinates;
}
