package com.travel.discovery.service;

import com.travel.discovery.dto.request.RegisterRequest;
import com.travel.discovery.dto.request.UpdateUserRequest;
import com.travel.discovery.dto.response.PageResponse;
import com.travel.discovery.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {

    PageResponse<UserResponse> getUsers(Pageable pageable);

    UserResponse getUser(Long id);

    UserResponse createUser(RegisterRequest request);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    /**
     * Deletes a user along with everything they own — bookings, reviews and
     * favourites — so the {@code user_id} foreign keys don't block the delete.
     */
    void deleteUser(Long id);
}