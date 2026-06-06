package com.travel.discovery.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Admin-facing user update. Both fields are optional (partial update); only the
 * non-null ones are applied. Validation applies when a value is present.
 */
@Data
public class UpdateUserRequest {

    @Size(min = 2, max = 100, message = "Full name must be 2–100 characters")
    private String fullName;

    @Email(message = "Email must be valid")
    private String email;

    private String avatarUrl;

}