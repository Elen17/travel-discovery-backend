package com.travel.discovery.controller;

import com.travel.discovery.dto.request.PlannerChatRequest;
import com.travel.discovery.dto.request.PlannerPlanRequest;
import com.travel.discovery.dto.response.PlannerChatResponse;
import com.travel.discovery.dto.response.PlannerMessageResponse;
import com.travel.discovery.dto.response.PlannerPlanResponse;
import com.travel.discovery.security.CurrentUserService;
import com.travel.discovery.service.PlannerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/planner")
@RequiredArgsConstructor
public class PlannerController {

    private final PlannerService plannerService;
    private final CurrentUserService currentUserService;

    /** Persists one chat message. Requires authentication. */
    @PostMapping("/chat")
    public ResponseEntity<PlannerChatResponse> chat(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody PlannerChatRequest request
    ) {
        Long userId = currentUserService.getCurrentUserId(userDetails);
        return ResponseEntity.ok(plannerService.chat(userId, request));
    }

    /** Loads stored messages for a session. Public (used by shared links). */
    @GetMapping("/history")
    public ResponseEntity<List<PlannerMessageResponse>> history(
        @RequestParam String sessionToken
    ) {
        return ResponseEntity.ok(plannerService.getHistory(sessionToken));
    }

    /**
     * Lists the caller's saved plans. Public at the security layer: when a valid
     * Bearer token is present the plans for that user are returned, otherwise the
     * list is empty.
     */
    @GetMapping("/plans")
    public ResponseEntity<List<PlannerPlanResponse>> plans(
    ) {
        return ResponseEntity.ok(plannerService.getPlans());
    }

    /** Creates a new saved plan for the authenticated user. */
    @PostMapping("/plans")
    public ResponseEntity<PlannerPlanResponse> createPlan(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody PlannerPlanRequest request
    ) {
        Long userId = currentUserService.getCurrentUserId(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(plannerService.createPlan(userId, request));
    }
}
