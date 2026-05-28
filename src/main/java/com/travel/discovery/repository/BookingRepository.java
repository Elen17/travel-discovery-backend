package com.travel.discovery.repository;

import com.travel.discovery.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    // FIX: userId is now Long (FK to users.id), not String
    Page<Booking> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Optional<Booking> findByIdAndUserId(Long id, Long userId);
}
