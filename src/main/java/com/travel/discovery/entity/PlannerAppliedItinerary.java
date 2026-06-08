package com.travel.discovery.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "planner_applied_itineraries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlannerAppliedItinerary extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private PlannerSession session;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
