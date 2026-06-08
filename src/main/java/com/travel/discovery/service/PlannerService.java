package com.travel.discovery.service;

import com.travel.discovery.dto.request.PlannerChatRequest;
import com.travel.discovery.dto.request.PlannerPlanRequest;
import com.travel.discovery.dto.response.PlannerChatResponse;
import com.travel.discovery.dto.response.PlannerMessageResponse;
import com.travel.discovery.dto.response.PlannerPlanResponse;

import java.util.List;

public interface PlannerService {

    /**
     * Persists one chat message for the authenticated user. Creates a new
     * session when no sessionToken is supplied, otherwise appends to the
     * existing (owned) session.
     */
    PlannerChatResponse chat(Long userId, PlannerChatRequest request);

    /**
     * Returns all messages of a session in chronological order. Public: no
     * authentication or ownership check (used by shared planner links).
     */
    List<PlannerMessageResponse> getHistory(String sessionToken);

    /**
     * Returns every saved plan for the given user (sessions with their messages
     * and applied itineraries), newest first. Returns an empty list when userId
     * is null (no authenticated caller).
     */
    List<PlannerPlanResponse> getPlans();

    /**
     * Creates a new saved plan (session) for the authenticated user, persisting
     * any supplied messages and applied itineraries, and returns it.
     */
    PlannerPlanResponse createPlan(Long userId, PlannerPlanRequest request);
}
