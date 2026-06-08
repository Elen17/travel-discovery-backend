package com.travel.discovery.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String fullName;
    private String email;
    private String avatarUrl;
    private Instant createdAt;
    private String role;
}
