package com.example.ecommerce.dto;

import jakarta.validation.constraints.Email;

public record UpdateAdminUserRequest(
        String fullName,
        @Email String email,
        String password,
        String role
) {
}

