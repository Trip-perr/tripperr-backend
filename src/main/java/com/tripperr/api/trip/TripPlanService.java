package com.tripperr.api.trip;

import com.tripperr.api.common.PageResponse;
import com.tripperr.api.exception.BadRequestException;
import com.tripperr.api.exception.ForbiddenException;
import com.tripperr.api.exception.ResourceNotFoundException;
import com.tripperr.api.trip.dto.TripPlanRequest;
import com.tripperr.api.trip.dto.TripPlanResponse;
import com.tripperr.api.trip.mapper.TripPlanMapper;
import com.tripperr.api.trip.model.TripPlan;
import com.tripperr.api.trip.model.Visibility;
import com.tripperr.api.trip.repository.TripPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TripPlanService {

    private final TripPlanRepository repository;
    private final TripPlanMapper mapper;

    public TripPlanResponse create(String ownerId, TripPlanRequest req) {
        validateDates(req);
        TripPlan entity = mapper.toEntity(req, ownerId);
        return mapper.toResponse(repository.save(entity));
    }

    public TripPlanResponse get(String userId, String id) {
        TripPlan plan = repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("TripPlan", id));
        assertReadAccess(plan, userId);
        return mapper.toResponse(plan);
    }

    public PageResponse<TripPlanResponse> listMine(String ownerId, Pageable pageable) {
        Page<TripPlan> page = repository.findByOwnerId(ownerId, pageable);
        return PageResponse.from(page.map(mapper::toResponse));
    }

    public PageResponse<TripPlanResponse> listAccessible(String userId, String query, Pageable pageable) {
        Page<TripPlan> page = (query == null || query.isBlank())
                ? repository.findAccessibleByUser(userId, pageable)
                : repository.searchAccessibleByUser(userId, java.util.regex.Pattern.quote(query.trim()), pageable);
        return PageResponse.from(page.map(mapper::toResponse));
    }

    public TripPlanResponse update(String userId, String id, TripPlanRequest req) {
        validateDates(req);
        TripPlan plan = repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("TripPlan", id));
        assertWriteAccess(plan, userId);
        mapper.applyUpdate(plan, req);
        return mapper.toResponse(repository.save(plan));
    }

    public void delete(String userId, String id) {
        TripPlan plan = repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("TripPlan", id));
        if (!plan.getOwnerId().equals(userId)) {
            throw new ForbiddenException("Only the owner can delete a trip plan");
        }
        repository.delete(plan);
    }

    private void validateDates(TripPlanRequest req) {
        if (req.endDate().isBefore(req.startDate())) {
            throw new BadRequestException("endDate must be on or after startDate");
        }
    }

    private void assertReadAccess(TripPlan plan, String userId) {
        if (plan.getVisibility() == Visibility.PUBLIC) return;
        if (plan.getOwnerId().equals(userId)) return;
        if (plan.getCollaborators() != null && plan.getCollaborators().contains(userId)) return;
        throw new ForbiddenException("You do not have access to this trip plan");
    }

    private void assertWriteAccess(TripPlan plan, String userId) {
        if (plan.getOwnerId().equals(userId)) return;
        if (plan.getCollaborators() != null && plan.getCollaborators().contains(userId)) return;
        throw new ForbiddenException("You do not have write access to this trip plan");
    }
}
