package com.traveldiscovery.repository;

import com.traveldiscovery.entity.Event;
import com.traveldiscovery.entity.enums.EventCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Page<Event> findByEventDateAndCategory(LocalDate eventDate, EventCategory category, Pageable pageable);
    Page<Event> findByEventDate(LocalDate eventDate, Pageable pageable);
    Page<Event> findByCategory(EventCategory category, Pageable pageable);
    Page<Event> findByEventDateBetween(LocalDate from, LocalDate to, Pageable pageable);
}
