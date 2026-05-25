package com.traveldiscovery.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(min = 2, max = 100)
    private String fullName;

    private String avatarUrl;
    private String homeCountry;
    private String preferredCurrency;
    private String preferredLanguage;
    private Boolean notificationBookings;
    private Boolean notificationInspiration;
}
