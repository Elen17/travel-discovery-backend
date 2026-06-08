package com.travel.discovery.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class PlannerPlanRequest {

    /** Optional plan name; when omitted the title is derived from the first user message. */
    private String title;

    private String description;

    /** One of: iceland, tuscany, kyoto, amalfi (validated in the service). */
    private String explorationId;

    /** Numeric duration, e.g. 12 (paired with {@link #type}). */
    private Integer duration;

    /** Plan granularity: conceptually "day" or "hour". */
    private String type;

    private Integer travelersCount;

    private String imageUrl;

    @Valid
    private List<MessageItem> messages;

    @Valid
    private List<AppliedItineraryItem> appliedItineraries;

    @Data
    public static class MessageItem {
        @NotBlank(message = "Message content is required")
        private String content;

        /** "user" or "assistant"; defaults to "user" when omitted. */
        private String role;
    }

    @Data
    public static class AppliedItineraryItem {
        @NotBlank(message = "Itinerary title is required")
        private String title;

        private String description;
    }
}