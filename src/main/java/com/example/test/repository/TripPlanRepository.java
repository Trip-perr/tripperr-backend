package com.example.test.repository;

import com.example.test.model.TripPlan;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface TripPlanRepository extends MongoRepository<TripPlan, Long> {

    TripPlan save(TripPlan tripPlan);

    List<TripPlan> findAll();

    Optional<TripPlan> findById(String id);

    void deleteById(String id);
}