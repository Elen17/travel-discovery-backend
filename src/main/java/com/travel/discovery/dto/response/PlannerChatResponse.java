package com.travel.discovery.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PlannerChatResponse {

    /** Session id — existing one echoed back, or a newly created one. */
    private String sessionToken;

    /**
     * Always empty for synced messages: the frontend already has the Gemini
     * reply and ignores this field. Kept for contract compatibility.
     */
    @Builder.Default
    private String reply = "";

    /** Optional/legacy; the frontend ignores this for synced messages. */
    private List<PlannerSuggestionResponse> suggestions;
}