package com.travel.discovery.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvatarUploadResponse {
    /** Opaque id of the staged (not yet confirmed) image. Send this back in PUT /me to save it. */
    private String tempId;
    /** URL the client can use to preview the staged image before saving. */
    private String previewUrl;
}