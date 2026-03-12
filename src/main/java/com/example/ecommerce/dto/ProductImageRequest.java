package com.example.ecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProductImageRequest(
        @NotBlank
        @Size(max = 2000)
        String imageUrl,

        @NotNull
        Boolean primaryImage,

        @NotNull
        Integer displayOrder
) {}