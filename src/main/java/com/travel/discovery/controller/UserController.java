package com.travel.discovery.controller;

import com.travel.discovery.dto.request.UpdateProfileRequest;
import com.travel.discovery.dto.response.UserResponse;
import com.travel.discovery.entity.User;
import com.travel.discovery.exception.ResourceNotFoundException;
import com.travel.discovery.repository.UserRepository;
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
        return ResponseEntity.ok(toResponse(userRepository.save(user)));
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
