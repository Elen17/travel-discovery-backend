package com.travel.discovery.service.impl;

import com.travel.discovery.dto.request.RegisterRequest;
import com.travel.discovery.dto.request.UpdateUserRequest;
import com.travel.discovery.dto.response.PageResponse;
import com.travel.discovery.dto.response.UserResponse;
import com.travel.discovery.entity.User;
import com.travel.discovery.entity.enums.UserRole;
import com.travel.discovery.exception.ConflictException;
import com.travel.discovery.exception.ResourceNotFoundException;
import com.travel.discovery.mapper.UserMapper;
import com.travel.discovery.repository.BookingRepository;
import com.travel.discovery.repository.FavouritesRepository;
import com.travel.discovery.repository.ReviewRepository;
import com.travel.discovery.repository.UserRepository;
import com.travel.discovery.repository.UserRoleRepository;
import com.travel.discovery.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;
    private final FavouritesRepository favouritesRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public PageResponse<UserResponse> getUsers(Pageable pageable) {
        Page<User> page = userRepository.findAllNonAdminUsers(pageable);
        return PageResponse.<UserResponse>builder()
                .content(page.getContent().stream().map(userMapper::toResponse).toList())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    public UserResponse getUser(Long id) {
        return userMapper.toResponse(findUserOrThrow(id));
    }

    @Override
    @Transactional
    public UserResponse createUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                // Attach the USER role by a managed reference: getReferenceById returns a
                // lazy proxy carrying just the id, so it sets role_id without an extra
                // SELECT and without the "transient instance" risk of a hand-built entity.
                .role(userRoleRepository.getReferenceById(UserRole.USER.getId()))
                .build();

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUserOrThrow(id);

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }
        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new ConflictException("Email already registered: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        if (request.getAvatarUrl() != null) {
            user.setAvatarUrl(request.getAvatarUrl());
        }

        return userMapper.toResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = findUserOrThrow(id);

        // Delete everything the user owns first — bookings, reviews and favourites
        // all have a NOT NULL user_id FK that would otherwise block the delete.
        bookingRepository.deleteByUserId(id);
        reviewRepository.deleteByUserId(id);
        favouritesRepository.deleteByUserId(id);

        userRepository.delete(user);
    }

    // finds non-admin user by id
    private User findUserOrThrow(Long id) {
        return userRepository.findNonAdminUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }
}