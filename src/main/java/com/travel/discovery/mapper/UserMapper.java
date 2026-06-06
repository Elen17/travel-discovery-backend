package com.travel.discovery.mapper;

import com.travel.discovery.dto.response.UserResponse;
import com.travel.discovery.entity.User;
import org.springframework.stereotype.Component;

/** Single place that turns a {@link User} into the API-facing {@link UserResponse}. */
@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        return UserResponse.builder()
            .id(user.getId())
            .fullName(user.getFullName())
            .email(user.getEmail())
            .avatarUrl(user.getAvatarUrl())
            .createdAt(user.getCreatedAt())
            .build();
    }
}