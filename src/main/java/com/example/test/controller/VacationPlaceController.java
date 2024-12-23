package com.example.test.controller;

import com.example.test.model.VacationPlace;
import com.example.test.service.VacationPlaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vacation-places")
public class VacationPlaceController {
    @Autowired
    private VacationPlaceService vacationPlaceService;

    @GetMapping
    public ResponseEntity<List<VacationPlace>> getAllVacationPlaces() {
        List<VacationPlace> vacationPlaces = vacationPlaceService.getAllVacationPlaces();
        return ResponseEntity.ok(vacationPlaces);
    }

    @PostMapping
    public ResponseEntity<VacationPlace> createVacationPlace(@RequestBody VacationPlace vacationPlace) {
        VacationPlace createdVacationPlace = vacationPlaceService.saveVacationPlace(vacationPlace);
        return ResponseEntity.ok(createdVacationPlace);
    }
}