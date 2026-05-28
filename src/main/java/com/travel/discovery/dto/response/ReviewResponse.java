package com.travel.discovery.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ReviewResponse {
    private Long id;
    private String reviewerName;
    private String reviewerAvatarUrl;
    private Short rating;
    private String comment;
    private Instant createdAt;
}
