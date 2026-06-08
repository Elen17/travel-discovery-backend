package com.travel.discovery.repository;

import com.travel.discovery.entity.PlannerMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlannerMessageRepository extends JpaRepository<PlannerMessage, Long> {
    List<PlannerMessage> findBySessionIdOrderByIdAsc(Long sessionId);
}