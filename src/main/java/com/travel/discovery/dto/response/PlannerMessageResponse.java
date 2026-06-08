package com.travel.discovery.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlannerMessageResponse {
    /** "user" or "assistant". */
    private String role;
    private String content;
}