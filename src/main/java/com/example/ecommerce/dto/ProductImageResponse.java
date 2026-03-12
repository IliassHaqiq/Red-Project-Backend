package com.example.ecommerce.dto;

public record ProductImageResponse(
        Long id,
        String imageUrl,
        boolean primaryImage,
        Integer displayOrder
) {}