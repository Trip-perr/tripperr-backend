package com.tripperr.api.place;

import com.tripperr.api.common.PageResponse;
import com.tripperr.api.exception.ResourceNotFoundException;
import com.tripperr.api.place.dto.VacationPlaceRequest;
import com.tripperr.api.place.dto.VacationPlaceResponse;
import com.tripperr.api.place.mapper.VacationPlaceMapper;
import com.tripperr.api.place.model.VacationPlace;
import com.tripperr.api.place.repository.VacationPlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VacationPlaceService {

    private final VacationPlaceRepository repository;
    private final VacationPlaceMapper mapper;

    public VacationPlaceResponse create(VacationPlaceRequest req) {
        return mapper.toResponse(repository.save(mapper.toEntity(req)));
    }

    public VacationPlaceResponse update(String id, VacationPlaceRequest req) {
        VacationPlace existing = repository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("VacationPlace", id));
        mapper.applyUpdate(existing, req);
        return mapper.toResponse(repository.save(existing));
    }

    public VacationPlaceResponse get(String id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> ResourceNotFoundException.of("VacationPlace", id));
    }

    public void delete(String id) {
        if (!repository.existsById(id)) {
            throw ResourceNotFoundException.of("VacationPlace", id);
        }
        repository.deleteById(id);
    }

    public PageResponse<VacationPlaceResponse> list(String country, String city, Pageable pageable) {
        Page<VacationPlace> page;
        if (country != null && !country.isBlank()) {
            page = repository.findByCountryIgnoreCase(country, pageable);
        } else if (city != null && !city.isBlank()) {
            page = repository.findByCityIgnoreCase(city, pageable);
        } else {
            page = repository.findAll(pageable);
        }
        return PageResponse.from(page.map(mapper::toResponse));
    }

    public PageResponse<VacationPlaceResponse> search(String query, double minRating, Pageable pageable) {
        String regex = (query == null || query.isBlank()) ? "" : java.util.regex.Pattern.quote(query.trim());
        Page<VacationPlace> page = repository.search(regex, minRating, pageable);
        return PageResponse.from(page.map(mapper::toResponse));
    }
}
