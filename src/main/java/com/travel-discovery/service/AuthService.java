package com.traveldiscovery.service;

import com.traveldiscovery.dto.request.LoginRequest;
import com.traveldiscovery.dto.request.RegisterRequest;
import com.traveldiscovery.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
