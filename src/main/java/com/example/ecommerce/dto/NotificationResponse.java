package com.example.ecommerce.dto;

import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        String message,
        String type,
        boolean read,
        LocalDateTime createdAt,
        String relatedEntityType,
        Long relatedEntityId
) {}
