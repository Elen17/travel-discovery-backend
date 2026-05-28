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
    private String homeCountry;
    private String preferredCurrency;
    private String preferredLanguage;
    private String role;
    private Boolean notificationBookings;
    private Boolean notificationInspiration;
    private Instant createdAt;
}
