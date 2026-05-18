package com.tripperr.api.trip.mapper;

import com.tripperr.api.trip.dto.ActivityDto;
import com.tripperr.api.trip.dto.TripDayDto;
import com.tripperr.api.trip.dto.TripPlanRequest;
import com.tripperr.api.trip.dto.TripPlanResponse;
import com.tripperr.api.trip.model.Activity;
import com.tripperr.api.trip.model.TripDay;
import com.tripperr.api.trip.model.TripPlan;
import com.tripperr.api.trip.model.Visibility;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class TripPlanMapper {

    public TripPlan toEntity(TripPlanRequest req, String ownerId) {
        return TripPlan.builder()
                .title(req.title())
                .description(req.description())
                .destination(req.destination())
                .country(req.country())
                .startDate(req.startDate())
                .endDate(req.endDate())
                .estimatedBudget(req.estimatedBudget())
                .currency(req.currency())
                .tripType(req.tripType())
                .coverImage(req.coverImage())
                .tags(req.tags() != null ? new HashSet<>(req.tags()) : new HashSet<>())
                .visibility(req.visibility() != null ? req.visibility() : Visibility.PRIVATE)
                .ownerId(ownerId)
                .collaborators(req.collaborators() != null ? new HashSet<>(req.collaborators()) : new HashSet<>())
                .itineraryDays(req.itineraryDays() != null
                        ? req.itineraryDays().stream().map(this::toDay).toList()
                        : List.of())
                .build();
    }

    public void applyUpdate(TripPlan target, TripPlanRequest req) {
        target.setTitle(req.title());
        target.setDescription(req.description());
        target.setDestination(req.destination());
        target.setCountry(req.country());
        target.setStartDate(req.startDate());
        target.setEndDate(req.endDate());
        target.setEstimatedBudget(req.estimatedBudget());
        target.setCurrency(req.currency());
        target.setTripType(req.tripType());
        target.setCoverImage(req.coverImage());
        target.setTags(req.tags() != null ? new HashSet<>(req.tags()) : new HashSet<>());
        target.setVisibility(req.visibility() != null ? req.visibility() : target.getVisibility());
        target.setCollaborators(req.collaborators() != null ? new HashSet<>(req.collaborators()) : new HashSet<>());
        target.setItineraryDays(req.itineraryDays() != null
                ? req.itineraryDays().stream().map(this::toDay).toList()
                : List.of());
    }

    public TripPlanResponse toResponse(TripPlan t) {
        return new TripPlanResponse(
                t.getId(),
                t.getTitle(),
                t.getDescription(),
                t.getDestination(),
                t.getCountry(),
                t.getStartDate(),
                t.getEndDate(),
                t.getEstimatedBudget(),
                t.getCurrency(),
                t.getTripType(),
                t.getCoverImage(),
                t.getTags() != null ? t.getTags() : Set.of(),
                t.getVisibility(),
                t.getOwnerId(),
                t.getCollaborators() != null ? t.getCollaborators() : Set.of(),
                t.getItineraryDays() != null ? t.getItineraryDays().stream().map(this::toDayDto).toList() : List.of(),
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }

    private TripDay toDay(TripDayDto dto) {
        return TripDay.builder()
                .dayNumber(dto.dayNumber())
                .date(dto.date())
                .activities(dto.activities() != null
                        ? dto.activities().stream().map(this::toActivity).toList()
                        : List.of())
                .build();
    }

    private Activity toActivity(ActivityDto dto) {
        return Activity.builder()
                .title(dto.title())
                .description(dto.description())
                .location(dto.location())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .category(dto.category())
                .notes(dto.notes())
                .estimatedCost(dto.estimatedCost())
                .build();
    }

    private TripDayDto toDayDto(TripDay d) {
        return new TripDayDto(
                d.getDayNumber(),
                d.getDate(),
                d.getActivities() != null ? d.getActivities().stream().map(this::toActivityDto).toList() : List.of()
        );
    }

    private ActivityDto toActivityDto(Activity a) {
        return new ActivityDto(
                a.getTitle(),
                a.getDescription(),
                a.getLocation(),
                a.getStartTime(),
                a.getEndTime(),
                a.getCategory(),
                a.getNotes(),
                a.getEstimatedCost()
        );
    }
}
