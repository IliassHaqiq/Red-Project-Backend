package com.example.ecommerce.dto;

import com.example.ecommerce.entity.User;

import java.util.Set;

public record UserSummaryResponse(
        Long id,
        String email,
        String fullName,
        Set<String> roles
) {
    public static UserSummaryResponse fromEntity(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRoles().stream()
                        .map(r -> r.getName().name())
                        .collect(java.util.stream.Collectors.toSet())
        );
    }
}

