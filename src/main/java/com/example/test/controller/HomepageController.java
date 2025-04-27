package com.example.test.controller;

import com.example.test.model.VacationPlace;
import com.example.test.service.HomepageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/homepage")
public class HomepageController {

    @Autowired
    private HomepageService homepageService;

    @GetMapping("/getDetailsVacationPlace")
    public ResponseEntity<VacationPlace> getVacationPlace(@RequestParam String vacationPlaceName) {
        Optional<VacationPlace> vacationPlace = homepageService.getDetailsOfVacationPlace(vacationPlaceName);
        return vacationPlace.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
