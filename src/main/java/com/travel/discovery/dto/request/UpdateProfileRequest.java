package com.travel.discovery.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @Size(min = 2, max = 100)
    private String fullName;

    /** Id of a previously staged image (from POST /me/avatar/temp) to confirm and save as the avatar. */
    private String avatarTempId;
}
