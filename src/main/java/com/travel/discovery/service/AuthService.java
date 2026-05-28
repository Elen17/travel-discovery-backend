package com.travel.discovery.service;

import com.travel.discovery.dto.request.LoginRequest;
import com.travel.discovery.dto.request.RegisterRequest;
import com.travel.discovery.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void logout(String accessToken);
}
