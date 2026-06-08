package com.travel.discovery.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PlannerChatRequest {

    @NotBlank(message = "Message is required")
    private String message;

    /** Existing backend session id. Omit on the first message of a new session. */
    private String sessionToken;

    /** One of: iceland, tuscany, kyoto, amalfi (validated in the service). */
    private String explorationId;

    /** "user" or "assistant"; defaults to "user" when omitted. */
    private String role;
}
