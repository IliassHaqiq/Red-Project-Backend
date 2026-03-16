package com.example.ecommerce.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false, length = 500)
    private String message;

    @Column(nullable = false, length = 100)
    private String type; // ORDER_CREATED, ORDER_STATUS_CHANGED, PRODUCT_CREATED, etc.

    @Builder.Default
    @Column(name = "is_read", nullable = false)
    private boolean read = false;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(length = 100)
    private String relatedEntityType; // ORDER, PRODUCT, etc.

    private Long relatedEntityId; // ID de l'entité liée (ex: orderId)
}
