package com.travel.discovery.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Optional/legacy itinerary suggestion shape. The frontend generates these
 * client-side via Gemini and does not rely on the backend to produce them,
 * but the type is kept so the contract stays stable.
 */
@Data
@Builder
public class PlannerSuggestionResponse {
    private String id;
    private String title;
    /** One of: nature, wellness, adventure. */
    private String category;
    private String duration;
    private String description;
    private List<String> steps;
}