package com.traveldiscovery.service;

import com.traveldiscovery.dto.request.UpdateProfileRequest;
import com.traveldiscovery.dto.response.UserResponse;

public interface UserService {
    UserResponse getMe(String email);
    UserResponse updateMe(String email, UpdateProfileRequest request);
}
