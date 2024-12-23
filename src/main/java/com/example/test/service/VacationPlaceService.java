package com.example.test.service;

import com.example.test.model.VacationPlace;
import com.example.test.repository.VacationPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VacationPlaceService {
    @Autowired
    private VacationPlaceRepository vacationPlaceRepository;

    public List<VacationPlace> getAllVacationPlaces() {
        return vacationPlaceRepository.findAll();
    }

    public VacationPlace saveVacationPlace(VacationPlace vacationPlace) {
        return vacationPlaceRepository.save(vacationPlace);
    }
}