package com.tripperr.api.place.repository;

import com.tripperr.api.place.model.VacationPlace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface VacationPlaceRepository extends MongoRepository<VacationPlace, String> {

    Page<VacationPlace> findByCountryIgnoreCase(String country, Pageable pageable);

    Page<VacationPlace> findByCityIgnoreCase(String city, Pageable pageable);

    @Query("{ $and: [ " +
            "  { $or: [ " +
            "    { name: { $regex: ?0, $options: 'i' } }, " +
            "    { city: { $regex: ?0, $options: 'i' } }, " +
            "    { country: { $regex: ?0, $options: 'i' } }, " +
            "    { tags: { $regex: ?0, $options: 'i' } }, " +
            "    { categories: { $regex: ?0, $options: 'i' } } " +
            "  ] }, " +
            "  { rating: { $gte: ?1 } } " +
            "] }")
    Page<VacationPlace> search(String text, double minRating, Pageable pageable);
}
