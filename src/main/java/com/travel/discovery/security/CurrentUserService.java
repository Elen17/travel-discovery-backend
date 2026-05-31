package com.travel.discovery.security;

import com.travel.discovery.entity.User;
import com.travel.discovery.exception.ResourceNotFoundException;
import com.travel.discovery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Resolves the authenticated principal (a Spring Security {@link UserDetails},
 * whose username is the email) to our own {@link User} entity. Centralises the
 * lookup that controllers previously duplicated as a private {@code resolveUserId}.
 */
@Service
@RequiredArgsConstructor
public class CurrentUserService {

    private final UserRepository userRepository;

    public User getCurrentUser(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public Long getCurrentUserId(UserDetails userDetails) {
        return getCurrentUser(userDetails).getId();
    }
}