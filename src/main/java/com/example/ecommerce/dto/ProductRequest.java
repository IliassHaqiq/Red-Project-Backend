package com.example.ecommerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

public record ProductRequest(
        @NotBlank
        String name,

        String description,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal price,

        @NotNull
        @Min(0)
        Integer stock,

        @NotNull
        Long categoryId,

        @Valid
        List<ProductImageRequest> images
) {}