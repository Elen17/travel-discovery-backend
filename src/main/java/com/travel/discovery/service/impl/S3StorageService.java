package com.travel.discovery.service.impl;

import com.travel.discovery.config.properties.S3Properties;
import com.travel.discovery.dto.response.AvatarUploadResponse;
import com.travel.discovery.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@ConditionalOnProperty(name = "storage.provider", havingValue = "s3")
@RequiredArgsConstructor
public class S3StorageService extends AbstractStorageService {

    private final S3Client s3;
    private final S3Properties props;

    @Override
    public AvatarUploadResponse stageAvatar(MultipartFile file) {
        validate(file);

        String tempId = UUID.randomUUID().toString();
        String key = props.getTempPrefix() + tempId;

        PutObjectRequest request = PutObjectRequest.builder()
            .bucket(props.getBucket())
            .key(key)
            .contentType(file.getContentType())
            .contentLength(file.getSize())
            .build();

        try {
            s3.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
        } catch (IOException e) {
            throw new BadRequestException("Could not read the uploaded file");
        } catch (S3Exception e) {
            log.error("Failed to stage avatar to S3 key {}: {}", key, e.getMessage(), e);
            throw new RuntimeException("Failed to upload image", e);
        }

        return AvatarUploadResponse.builder()
            .tempId(tempId)
            .previewUrl(publicUrl(key))
            .build();
    }

    @Override
    public String confirmAvatar(String tempId, String previousAvatarUrl) {
        String tempKey = requireStagedKey(tempId);

        String finalId = UUID.randomUUID().toString();
        String finalKey = props.getAvatarPrefix() + finalId;

        try {
            s3.copyObject(CopyObjectRequest.builder()
                .sourceBucket(props.getBucket())
                .sourceKey(tempKey)
                .destinationBucket(props.getBucket())
                .destinationKey(finalKey)
                .build());
        } catch (S3Exception e) {
            log.error("Failed to copy {} -> {}: {}", tempKey, finalKey, e.getMessage(), e);
            throw new RuntimeException("Failed to save image", e);
        }

        // Clean up the staged copy, then the user's previous avatar (best-effort).
        deleteQuietly(tempKey);
        deletePreviousAvatar(previousAvatarUrl);

        return publicUrl(finalKey);
    }

    /**
     * Validate the tempId and confirm the staged object actually exists in the temp folder.
     * Returns the resolved S3 key, or throws {@link BadRequestException} if missing/expired.
     */
    private String requireStagedKey(String tempId) {
        if (!StringUtils.hasText(tempId)) {
            throw new BadRequestException("avatarTempId is required");
        }
        String tempKey = props.getTempPrefix() + tempId;
        try {
            s3.headObject(HeadObjectRequest.builder()
                .bucket(props.getBucket())
                .key(tempKey)
                .build());
        } catch (NoSuchKeyException e) {
            throw new BadRequestException("Staged image not found or expired. Please upload again.");
        } catch (S3Exception e) {
            // HeadObject returns a bodyless 404 that the SDK surfaces as a plain S3Exception.
            if (e.statusCode() == 404) {
                throw new BadRequestException("Staged image not found or expired. Please upload again.");
            }
            throw e;
        }
        return tempKey;
    }

    /** Delete the previous avatar if its URL points at an object in our bucket. */
    private void deletePreviousAvatar(String previousAvatarUrl) {
        if (!StringUtils.hasText(previousAvatarUrl)) {
            return;
        }
        String base = baseUrl();
        if (!previousAvatarUrl.startsWith(base + "/")) {
            return; // external URL (e.g. legacy avatar_url or Google photo) — leave it alone
        }
        String key = previousAvatarUrl.substring(base.length() + 1);
        if (key.startsWith(props.getAvatarPrefix())) {
            deleteQuietly(key);
        }
    }

    private void deleteQuietly(String key) {
        try {
            s3.deleteObject(DeleteObjectRequest.builder()
                .bucket(props.getBucket())
                .key(key)
                .build());
        } catch (S3Exception e) {
            // Non-fatal: log and move on so the user's update still succeeds.
            log.warn("Failed to delete S3 object {}: {}", key, e.getMessage());
        }
    }

    private String publicUrl(String key) {
        return baseUrl() + "/" + key;
    }

    /** Base URL (no trailing slash) for objects: configured CDN, or derived from bucket + region. */
    private String baseUrl() {
        if (StringUtils.hasText(props.getPublicBaseUrl())) {
            String base = props.getPublicBaseUrl();
            return base.endsWith("/") ? base.substring(0, base.length() - 1) : base;
        }
        return "https://" + props.getBucket() + ".s3." + props.getRegion() + ".amazonaws.com";
    }
}
