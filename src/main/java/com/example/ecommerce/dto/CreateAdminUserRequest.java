package com.example.ecommerce.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateAdminUserRequest(
        @NotBlank String fullName,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String role
) {
}

