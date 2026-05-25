package com.traveldiscovery.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ReviewResponse {
    private Long id;
    private String reviewerName;
    private String reviewerAvatarUrl;
    private Integer rating;
    private String comment;
    private Instant createdAt;
}
