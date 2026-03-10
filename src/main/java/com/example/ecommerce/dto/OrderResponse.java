package com.example.ecommerce.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, String customerEmail, String status, BigDecimal totalAmount,
                            LocalDateTime createdAt, List<OrderItemResponse> items) {}
