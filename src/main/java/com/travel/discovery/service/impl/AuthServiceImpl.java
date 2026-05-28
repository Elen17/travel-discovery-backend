package com.travel.discovery.service.impl;

import com.travel.discovery.dto.request.LoginRequest;
import com.travel.discovery.dto.request.RegisterRequest;
import com.travel.discovery.dto.response.AuthResponse;
import com.travel.discovery.dto.response.UserResponse;
import com.travel.discovery.entity.User;
import com.travel.discovery.exception.ConflictException;
import com.travel.discovery.repository.UserRepository;
import com.travel.discovery.security.JwtService;
import com.travel.discovery.security.TokenBlacklistService;
import com.travel.discovery.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
            .fullName(request.getFullName())
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .build();

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new RuntimeException("User not found after authentication"));

        return buildAuthResponse(user);
    }

    @Override
    public void logout(String accessToken) {
        try {
            Date expiration = jwtService.extractExpiration(accessToken);
            Duration ttl = Duration.between(Instant.now(), expiration.toInstant());
            tokenBlacklistService.blacklist(accessToken, ttl);
        } catch (Exception ignored) {
            // Invalid/expired token — nothing to blacklist; logout is idempotent.
        }
    }

    private AuthResponse buildAuthResponse(User user) {
        return AuthResponse.builder()
            .accessToken(jwtService.generateAccessToken(user.getEmail()))
            .refreshToken(jwtService.generateRefreshToken(user.getEmail()))
            .user(UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .createdAt(user.getCreatedAt())
                .build())
            .build();
    }
}
