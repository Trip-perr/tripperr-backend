package com.tripperr.api.trip.repository;

import com.tripperr.api.trip.model.TripPlan;
import com.tripperr.api.trip.model.Visibility;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface TripPlanRepository extends MongoRepository<TripPlan, String> {

    Page<TripPlan> findByOwnerId(String ownerId, Pageable pageable);

    Page<TripPlan> findByVisibility(Visibility visibility, Pageable pageable);

    @Query("{ $or: [ { ownerId: ?0 }, { collaborators: ?0 }, { visibility: 'PUBLIC' } ] }")
    Page<TripPlan> findAccessibleByUser(String userId, Pageable pageable);

    @Query("{ $and: [ " +
            "  { $or: [ { ownerId: ?0 }, { collaborators: ?0 }, { visibility: 'PUBLIC' } ] }, " +
            "  { $or: [ " +
            "    { title: { $regex: ?1, $options: 'i' } }, " +
            "    { destination: { $regex: ?1, $options: 'i' } }, " +
            "    { country: { $regex: ?1, $options: 'i' } }, " +
            "    { tags: { $regex: ?1, $options: 'i' } } " +
            "  ] } " +
            "] }")
    Page<TripPlan> searchAccessibleByUser(String userId, String text, Pageable pageable);
}
