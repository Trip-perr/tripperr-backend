package com.example.test.service;

import com.example.test.model.VacationPlace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class HomepageService {

    @Autowired
    VacationPlaceService vacationPlaceService;

    public Optional<VacationPlace> getDetailsOfVacationPlace(String vacationPlaceName) {
        return vacationPlaceService.getVacationPlace(vacationPlaceName);
    }
}
