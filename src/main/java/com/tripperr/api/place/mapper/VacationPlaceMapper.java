package com.tripperr.api.place.mapper;

import com.tripperr.api.place.dto.CoordinatesDto;
import com.tripperr.api.place.dto.VacationPlaceRequest;
import com.tripperr.api.place.dto.VacationPlaceResponse;
import com.tripperr.api.place.model.Coordinates;
import com.tripperr.api.place.model.VacationPlace;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class VacationPlaceMapper {

    public VacationPlace toEntity(VacationPlaceRequest req) {
        return VacationPlace.builder()
                .name(req.name())
                .city(req.city())
                .country(req.country())
                .description(req.description())
                .categories(req.categories() != null ? new HashSet<>(req.categories()) : new HashSet<>())
                .rating(req.rating())
                .images(req.images())
                .bestTimeToVisit(req.bestTimeToVisit())
                .estimatedBudget(req.estimatedBudget())
                .currency(req.currency())
                .tags(req.tags() != null ? new HashSet<>(req.tags()) : new HashSet<>())
                .coordinates(toCoordinates(req.coordinates()))
                .build();
    }

    public void applyUpdate(VacationPlace target, VacationPlaceRequest req) {
        target.setName(req.name());
        target.setCity(req.city());
        target.setCountry(req.country());
        target.setDescription(req.description());
        target.setCategories(req.categories() != null ? new HashSet<>(req.categories()) : new HashSet<>());
        target.setRating(req.rating());
        target.setImages(req.images());
        target.setBestTimeToVisit(req.bestTimeToVisit());
        target.setEstimatedBudget(req.estimatedBudget());
        target.setCurrency(req.currency());
        target.setTags(req.tags() != null ? new HashSet<>(req.tags()) : new HashSet<>());
        target.setCoordinates(toCoordinates(req.coordinates()));
    }

    public VacationPlaceResponse toResponse(VacationPlace p) {
        return new VacationPlaceResponse(
                p.getId(),
                p.getName(),
                p.getCity(),
                p.getCountry(),
                p.getDescription(),
                p.getCategories(),
                p.getRating(),
                p.getImages(),
                p.getBestTimeToVisit(),
                p.getEstimatedBudget(),
                p.getCurrency(),
                p.getTags(),
                p.getCoordinates() != null
                        ? new CoordinatesDto(p.getCoordinates().getLatitude(), p.getCoordinates().getLongitude())
                        : null,
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }

    private Coordinates toCoordinates(CoordinatesDto dto) {
        if (dto == null) return null;
        return new Coordinates(dto.latitude(), dto.longitude());
    }
}
