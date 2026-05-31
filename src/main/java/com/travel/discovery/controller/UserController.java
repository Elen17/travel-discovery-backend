package com.travel.discovery.controller;

import com.travel.discovery.dto.request.UpdateProfileRequest;
import com.travel.discovery.dto.response.AvatarUploadResponse;
import com.travel.discovery.dto.response.UserResponse;
import com.travel.discovery.entity.User;
import com.travel.discovery.repository.UserRepository;
import com.travel.discovery.security.CurrentUserService;
import com.travel.discovery.service.StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final CurrentUserService currentUserService;
    private final StorageService storageService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(toResponse(currentUserService.getCurrentUser(userDetails)));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody UpdateProfileRequest request
    ) {
        User user = currentUserService.getCurrentUser(userDetails);

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getAvatarTempId() != null && !request.getAvatarTempId().isBlank()) {
            // Promote the staged upload: copy temp -> final, delete temp + previous avatar.
            user.setAvatarUrl(storageService.confirmAvatar(request.getAvatarTempId(), user.getAvatarUrl()));
        }
        return ResponseEntity.ok(toResponse(userRepository.save(user)));
    }

    /**
     * Stage a profile image upload. The file is stored under a temporary key and is not yet
     * attached to the user — call PUT /me with the returned tempId to save it.
     */
    @PostMapping(value = "/me/avatar/temp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AvatarUploadResponse> uploadAvatar(
        @AuthenticationPrincipal UserDetails userDetails,
        @RequestPart("file") MultipartFile file
    ) {
        return ResponseEntity.ok(storageService.stageAvatar(file));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .avatarUrl(user.getAvatarUrl())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
