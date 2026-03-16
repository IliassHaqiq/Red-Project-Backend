package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateUserRoleRequest(
        @NotBlank String role
) {
}

