package com.travel.discovery.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class PlannerPlanResponse {
    /** The session token, used as the plan id. */
    private String id;
    /** Stored title, or derived from the first user message when none is set. */
    private String title;
    private String description;
    /** One of: iceland, tuscany, kyoto, amalfi (or null). */
    private String explorationId;
    /** Numeric duration, e.g. 12 (paired with {@link #type}). */
    private Integer duration;
    /** Plan granularity: conceptually "day" or "hour". */
    private String type;
    private Integer travelersCount;
    private String imageUrl;
    private Instant createdAt;
    private Instant updatedAt;
    private List<PlannerMessageResponse> messages;
    private List<PlannerAppliedItineraryResponse> appliedItineraries;
}
