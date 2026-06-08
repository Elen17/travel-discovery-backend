package com.travel.discovery.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "planner_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlannerSession extends BaseEntity {

    @Column(name = "session_token", nullable = false, unique = true, updatable = false)
    private String sessionToken;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** One of: iceland, tuscany, kyoto, amalfi (stored as a free-form string). */
    @Column(name = "exploration_id")
    private String explorationId;

    /** Optional user-given plan name; when null the title is derived from the first user message. */
    @Column(name = "title")
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Numeric duration, e.g. 12 (paired with {@link #type}). */
    @Column(name = "duration")
    private Integer duration;

    /** Plan granularity: conceptually "day" or "hour" (stored as a free-form string). */
    @Column(name = "type")
    private String type;

    @Column(name = "travelers_count")
    private Integer travelersCount;

    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;
}
