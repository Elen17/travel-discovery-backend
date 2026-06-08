package com.travel.discovery.repository;

import com.travel.discovery.entity.PlannerSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlannerSessionRepository extends JpaRepository<PlannerSession, Long> {
    Optional<PlannerSession> findBySessionToken(String sessionToken);

    List<PlannerSession> findByUserIdOrderByUpdatedAtDesc(Long userId);
}
