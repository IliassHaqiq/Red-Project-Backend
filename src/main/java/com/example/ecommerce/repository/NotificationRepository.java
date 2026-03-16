package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserEmailOrderByCreatedAtDesc(String email);
    List<Notification> findByUserEmailAndReadFalseOrderByCreatedAtDesc(String email);
    long countByUserEmailAndReadFalse(String email);
    void deleteByUserEmail(String email);
}
