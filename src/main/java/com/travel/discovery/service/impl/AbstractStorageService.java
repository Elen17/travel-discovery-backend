package com.travel.discovery.service.impl;

import com.travel.discovery.exception.BadRequestException;
import com.travel.discovery.service.StorageService;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/** Shared validation rules for every {@link StorageService} backend (local or S3). */
public abstract class AbstractStorageService implements StorageService {

    protected static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    protected static final long MAX_SIZE_BYTES = 5L * 1024 * 1024; // 5 MB

    protected void validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("No image file was provided");
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new BadRequestException("Image must be 5 MB or smaller");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Unsupported image type. Allowed: JPEG, PNG, WebP");
        }
    }
}