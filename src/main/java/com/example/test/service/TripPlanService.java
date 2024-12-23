package com.example.test.service;

import com.example.test.model.TripPlan;
import com.example.test.repository.TripPlanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TripPlanService {
    @Autowired
    private TripPlanRepository tripPlanRepository;

    public List<TripPlan> getAllTripPlans() {
        return tripPlanRepository.findAll();
    }

    public TripPlan saveTripPlan(TripPlan tripPlan) {
        return tripPlanRepository.save(tripPlan);
    }

    public Optional<TripPlan> getTripPlanById(String id) {
        return tripPlanRepository.findById(id);
    }

    public boolean deleteTripPlanById(String id) {
        try {
            tripPlanRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}