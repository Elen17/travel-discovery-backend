package com.traveldiscovery.controller;

import com.traveldiscovery.dto.request.UpdateProfileRequest;
import com.traveldiscovery.dto.response.UserResponse;
import com.traveldiscovery.entity.User;
import com.traveldiscovery.exception.ResourceNotFoundException;
import com.traveldiscovery.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.ok(toResponse(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateMe(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody UpdateProfileRequest request
    ) {
        User user = userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getFullName() != null)               user.setFullName(request.getFullName());
        if (request.getAvatarUrl() != null)               user.setAvatarUrl(request.getAvatarUrl());
        if (request.getHomeCountry() != null)             user.setHomeCountry(request.getHomeCountry());
        if (request.getPreferredCurrency() != null)       user.setPreferredCurrency(request.getPreferredCurrency());
        if (request.getPreferredLanguage() != null)       user.setPreferredLanguage(request.getPreferredLanguage());
        if (request.getNotificationBookings() != null)    user.setNotificationBookings(request.getNotificationBookings());
        if (request.getNotificationInspiration() != null) user.setNotificationInspiration(request.getNotificationInspiration());

        return ResponseEntity.ok(toResponse(userRepository.save(user)));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .avatarUrl(user.getAvatarUrl())
            .homeCountry(user.getHomeCountry())
            .preferredCurrency(user.getPreferredCurrency())
            .preferredLanguage(user.getPreferredLanguage())
            .role(user.getRole().name())
            .notificationBookings(user.getNotificationBookings())
            .notificationInspiration(user.getNotificationInspiration())
            .createdAt(user.getCreatedAt())
            .build();
    }
}
