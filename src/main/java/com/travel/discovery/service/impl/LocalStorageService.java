package com.travel.discovery.service.impl;

import com.travel.discovery.config.properties.LocalStorageProperties;
import com.travel.discovery.dto.response.AvatarUploadResponse;
import com.travel.discovery.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Filesystem-backed storage for local development — no S3 account needed.
 * Active when {@code storage.provider=local} (the default). Files are written under
 * {@code storage.local.directory} and served as static resources at {@code /uploads/**}.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "storage.provider", havingValue = "local", matchIfMissing = true)
@RequiredArgsConstructor
public class LocalStorageService extends AbstractStorageService {

    private static final String SERVE_PATH = "/uploads/";

    private final LocalStorageProperties props;

    @Override
    public AvatarUploadResponse stageAvatar(MultipartFile file) {
        validate(file);

        String tempId = UUID.randomUUID().toString();
        String key = props.getTempPrefix() + tempId;
        store(file, key);

        return AvatarUploadResponse.builder()
            .tempId(tempId)
            .previewUrl(publicUrl(key))
            .build();
    }

    @Override
    public String confirmAvatar(String tempId, String previousAvatarUrl) {
        if (!StringUtils.hasText(tempId)) {
            throw new BadRequestException("avatarTempId is required");
        }

        Path tempPath = resolve(props.getTempPrefix() + tempId);
        if (!Files.exists(tempPath)) {
            throw new BadRequestException("Staged image not found or expired. Please upload again.");
        }

        String finalId = UUID.randomUUID().toString();
        String finalKey = props.getAvatarPrefix() + finalId;
        Path finalPath = resolve(finalKey);

        try {
            Files.createDirectories(finalPath.getParent());
            Files.move(tempPath, finalPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("Failed to move {} -> {}: {}", tempPath, finalPath, e.getMessage(), e);
            throw new RuntimeException("Failed to save image", e);
        }

        deletePreviousAvatar(previousAvatarUrl);
        return publicUrl(finalKey);
    }

    private void store(MultipartFile file, String key) {
        Path dest = resolve(key);
        try {
            Files.createDirectories(dest.getParent());
            file.transferTo(dest);
        } catch (IOException e) {
            log.error("Failed to write {}: {}", dest, e.getMessage(), e);
            throw new RuntimeException("Failed to store file", e);
        }
    }

    /** Delete the previous avatar if its URL points at a file we manage. */
    private void deletePreviousAvatar(String previousAvatarUrl) {
        if (!StringUtils.hasText(previousAvatarUrl)) {
            return;
        }
        String prefix = props.getBaseUrl() + SERVE_PATH;
        if (!previousAvatarUrl.startsWith(prefix)) {
            return; // external URL (legacy/Google) — leave it alone
        }
        String key = previousAvatarUrl.substring(prefix.length());
        if (!key.startsWith(props.getAvatarPrefix())) {
            return;
        }
        try {
            Files.deleteIfExists(resolve(key));
        } catch (IOException e) {
            log.warn("Failed to delete local file for key {}: {}", key, e.getMessage());
        }
    }

    private String publicUrl(String key) {
        return props.getBaseUrl() + SERVE_PATH + key;
    }

    /** Resolve a storage key to an absolute path, guarding against path traversal. */
    private Path resolve(String key) {
        Path root = Paths.get(props.getDirectory()).toAbsolutePath().normalize();
        Path resolved = root.resolve(key).normalize();
        if (!resolved.startsWith(root)) {
            throw new BadRequestException("Invalid file path");
        }
        return resolved;
    }
}