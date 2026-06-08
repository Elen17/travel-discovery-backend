package com.travel.discovery.entity;

import com.travel.discovery.entity.enums.PlannerMessageRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "planner_messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlannerMessage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private PlannerSession session;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private PlannerMessageRole role;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;
}
