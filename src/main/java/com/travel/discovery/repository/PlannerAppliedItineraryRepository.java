package com.travel.discovery.repository;

import com.travel.discovery.entity.PlannerAppliedItinerary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlannerAppliedItineraryRepository extends JpaRepository<PlannerAppliedItinerary, Long> {
    List<PlannerAppliedItinerary> findBySessionIdOrderByIdAsc(Long sessionId);
}