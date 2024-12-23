package com.example.test.controller;

import com.example.test.model.TripPlan;
import com.example.test.service.TripPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/trip-plans")
public class TripPlanController {

    @Autowired
    private TripPlanService tripPlanService;

    @PostMapping("/createTripPlan")
    public ResponseEntity<TripPlan> createTripPlan(@RequestBody TripPlan tripPlan) {
        TripPlan createdTripPlan = tripPlanService.saveTripPlan(tripPlan);
        return ResponseEntity.ok(createdTripPlan);
    }

    @GetMapping("/getAllTripPlans")
    public ResponseEntity<List<TripPlan>> getAllTripPlans() {
        List<TripPlan> tripPlans = tripPlanService.getAllTripPlans();
        return ResponseEntity.ok(tripPlans);
    }

    @GetMapping("/getTripPlan/{id}")
    public ResponseEntity<TripPlan> getOneTripPlan(@PathVariable String id) {
        Optional<TripPlan> tripPlan = tripPlanService.getTripPlanById(id);
        return tripPlan.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/deleteTripPlan/{id}")
    public ResponseEntity<Void> deleteTripPlan(@PathVariable String id) {
        if (tripPlanService.deleteTripPlanById(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}