package com.travel.discovery.service;

import com.travel.discovery.dto.response.AvatarUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    /**
     * Stage an uploaded image under a temporary key. Validates content type and size,
     * but does not yet attach it to the user. Returns a generated tempId and a preview URL.
     */
    AvatarUploadResponse stageAvatar(MultipartFile file);

    /**
     * Promote a previously staged image to a permanent avatar: copies temp -> final under a
     * newly generated id, deletes the temp object, and deletes the user's previous avatar
     * (if it was stored by us). Returns the public URL of the final avatar.
     */
    String confirmAvatar(String tempId, String previousAvatarUrl);
}