package com.example.test.repository;

import com.example.test.model.VacationPlace;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface VacationPlaceRepository extends MongoRepository<VacationPlace, Long> {

    Optional<VacationPlace> findByName(String name);

    @Override
    VacationPlace save(VacationPlace vacationPlace);

    @Override
    List<VacationPlace> findAll();
}