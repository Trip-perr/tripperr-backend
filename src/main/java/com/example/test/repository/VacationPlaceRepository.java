package com.example.test.repository;

import com.example.test.model.VacationPlace;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VacationPlaceRepository extends MongoRepository<VacationPlace, Long> {

    @Override
    VacationPlace save(VacationPlace vacationPlace);

    @Override
    List<VacationPlace> findAll();
}