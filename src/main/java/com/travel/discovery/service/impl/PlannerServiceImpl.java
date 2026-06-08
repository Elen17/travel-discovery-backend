package com.travel.discovery.service.impl;

import com.travel.discovery.dto.request.PlannerChatRequest;
import com.travel.discovery.dto.request.PlannerPlanRequest;
import com.travel.discovery.dto.response.PlannerAppliedItineraryResponse;
import com.travel.discovery.dto.response.PlannerChatResponse;
import com.travel.discovery.dto.response.PlannerMessageResponse;
import com.travel.discovery.dto.response.PlannerPlanResponse;
import com.travel.discovery.entity.PlannerAppliedItinerary;
import com.travel.discovery.entity.PlannerMessage;
import com.travel.discovery.entity.PlannerSession;
import com.travel.discovery.entity.User;
import com.travel.discovery.entity.enums.PlannerMessageRole;
import com.travel.discovery.exception.BadRequestException;
import com.travel.discovery.exception.ForbiddenException;
import com.travel.discovery.exception.ResourceNotFoundException;
import com.travel.discovery.repository.PlannerAppliedItineraryRepository;
import com.travel.discovery.repository.PlannerMessageRepository;
import com.travel.discovery.repository.PlannerSessionRepository;
import com.travel.discovery.repository.UserRepository;
import com.travel.discovery.service.PlannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlannerServiceImpl implements PlannerService {

    private final PlannerSessionRepository sessionRepository;
    private final PlannerMessageRepository messageRepository;
    private final PlannerAppliedItineraryRepository appliedItineraryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public PlannerChatResponse chat(Long userId, PlannerChatRequest request) {
        PlannerMessageRole role = parseRole(request.getRole());

        PlannerSession session = (request.getSessionToken() == null || request.getSessionToken().isBlank())
            ? createSession(userId, request.getExplorationId())
            : resolveOwnedSession(request.getSessionToken(), userId);

        PlannerMessage message = PlannerMessage.builder()
            .session(session)
            .role(role)
            .content(request.getMessage())
            .build();
        messageRepository.save(message);

        return PlannerChatResponse.builder()
            .sessionToken(session.getSessionToken())
            .reply("")
            .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlannerMessageResponse> getHistory(String sessionToken) {
        PlannerSession session = sessionRepository.findBySessionToken(sessionToken)
            .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        return toMessageResponses(messageRepository.findBySessionIdOrderByIdAsc(session.getId()));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlannerPlanResponse> getPlans() {
        return sessionRepository.findAll().stream()
            .map(this::toPlanResponse)
            .toList();
    }

    @Override
    @Transactional
    public PlannerPlanResponse createPlan(Long userId, PlannerPlanRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        String title = (request.getTitle() == null || request.getTitle().isBlank())
            ? null
            : request.getTitle().trim();

        PlannerSession session = sessionRepository.save(PlannerSession.builder()
            .sessionToken("sess_" + UUID.randomUUID().toString().replace("-", ""))
            .user(user)
            .explorationId(normalizeExplorationId(request.getExplorationId()))
            .title(title)
            .description(request.getDescription())
            .duration(request.getDuration())
            .type(request.getType())
            .travelersCount(request.getTravelersCount())
            .imageUrl(request.getImageUrl())
            .build());

        if (request.getMessages() != null) {
            for (PlannerPlanRequest.MessageItem item : request.getMessages()) {
                messageRepository.save(PlannerMessage.builder()
                    .session(session)
                    .role(parseRole(item.getRole()))
                    .content(item.getContent())
                    .build());
            }
        }

        if (request.getAppliedItineraries() != null) {
            for (PlannerPlanRequest.AppliedItineraryItem item : request.getAppliedItineraries()) {
                appliedItineraryRepository.save(PlannerAppliedItinerary.builder()
                    .session(session)
                    .title(item.getTitle())
                    .description(item.getDescription())
                    .build());
            }
        }

        return toPlanResponse(session);
    }

    private PlannerPlanResponse toPlanResponse(PlannerSession session) {
        List<PlannerMessage> messages = messageRepository.findBySessionIdOrderByIdAsc(session.getId());
        List<PlannerAppliedItinerary> itineraries =
            appliedItineraryRepository.findBySessionIdOrderByIdAsc(session.getId());

        return PlannerPlanResponse.builder()
            .id(session.getSessionToken())
            .title(deriveTitle(session, messages))
            .description(session.getDescription())
            .explorationId(session.getExplorationId())
            .duration(session.getDuration())
            .type(session.getType())
            .travelersCount(session.getTravelersCount())
            .imageUrl(session.getImageUrl())
            .createdAt(session.getCreatedAt())
            .updatedAt(session.getUpdatedAt())
            .messages(toMessageResponses(messages))
            .appliedItineraries(itineraries.stream()
                .map(i -> PlannerAppliedItineraryResponse.builder()
                    .id(i.getId())
                    .title(i.getTitle())
                    .description(i.getDescription())
                    .build())
                .toList())
            .build();
    }

    private List<PlannerMessageResponse> toMessageResponses(List<PlannerMessage> messages) {
        return messages.stream()
            .map(m -> PlannerMessageResponse.builder()
                .role(m.getRole().toValue())
                .content(m.getContent())
                .build())
            .toList();
    }

    /** Stored title, falling back to the first user message's content. */
    private String deriveTitle(PlannerSession session, List<PlannerMessage> messages) {
        if (session.getTitle() != null && !session.getTitle().isBlank()) {
            return session.getTitle();
        }
        return messages.stream()
            .filter(m -> m.getRole() == PlannerMessageRole.USER)
            .map(PlannerMessage::getContent)
            .findFirst()
            .orElse(null);
    }

    private PlannerSession createSession(Long userId, String explorationId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        PlannerSession session = PlannerSession.builder()
            .sessionToken("sess_" + UUID.randomUUID().toString().replace("-", ""))
            .user(user)
            .explorationId(normalizeExplorationId(explorationId))
            .build();
        return sessionRepository.save(session);
    }

    private PlannerSession resolveOwnedSession(String sessionToken, Long userId) {
        PlannerSession session = sessionRepository.findBySessionToken(sessionToken)
            .orElseThrow(() -> new ResourceNotFoundException("Session not found"));

        if (!session.getUser().getId().equals(userId)) {
            throw new ForbiddenException("Session belongs to another user");
        }
        return session;
    }

    private PlannerMessageRole parseRole(String role) {
        try {
            return PlannerMessageRole.fromValue(role);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

    private String normalizeExplorationId(String explorationId) {
        if (explorationId == null || explorationId.isBlank()) {
            return null;
        }
        return explorationId.trim();
    }
}
