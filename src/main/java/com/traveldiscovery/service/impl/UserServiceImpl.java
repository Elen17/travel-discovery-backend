package com.traveldiscovery.service.impl;

import com.traveldiscovery.dto.request.UpdateProfileRequest;
import com.traveldiscovery.dto.response.UserResponse;
import com.traveldiscovery.entity.User;
import com.traveldiscovery.exception.ResourceNotFoundException;
import com.traveldiscovery.repository.UserRepository;
import com.traveldiscovery.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getMe(String email) {
        return toResponse(findByEmail(email));
    }

    @Override
    @Transactional
    public UserResponse updateMe(String email, UpdateProfileRequest request) {
        User user = findByEmail(email);
        if (request.getFullName() != null)               user.setFullName(request.getFullName());
        if (request.getAvatarUrl() != null)               user.setAvatarUrl(request.getAvatarUrl());
        if (request.getHomeCountry() != null)             user.setHomeCountry(request.getHomeCountry());
        if (request.getPreferredCurrency() != null)       user.setPreferredCurrency(request.getPreferredCurrency());
        if (request.getPreferredLanguage() != null)       user.setPreferredLanguage(request.getPreferredLanguage());
        if (request.getNotificationBookings() != null)    user.setNotificationBookings(request.getNotificationBookings());
        if (request.getNotificationInspiration() != null) user.setNotificationInspiration(request.getNotificationInspiration());
        return toResponse(userRepository.save(user));
    }

    private User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
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
