package com.example.ecommerce.service;

import com.example.ecommerce.dto.NotificationResponse;
import com.example.ecommerce.entity.Notification;
import com.example.ecommerce.entity.RoleName;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.NotificationRepository;
import com.example.ecommerce.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void createNotification(String userEmail, String message, String type, String relatedEntityType, Long relatedEntityId) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .relatedEntityType(relatedEntityType)
                .relatedEntityId(relatedEntityId)
                .build();

        notificationRepository.save(notification);
    }

    @Transactional
    public void notifyAllAdmins(String message, String type, String relatedEntityType, Long relatedEntityId) {
        List<User> admins = userRepository.findByRoleName(RoleName.ROLE_ADMIN);
        
        for (User admin : admins) {
            Notification notification = Notification.builder()
                    .user(admin)
                    .message(message)
                    .type(type)
                    .relatedEntityType(relatedEntityType)
                    .relatedEntityId(relatedEntityId)
                    .build();
            
            notificationRepository.save(notification);
        }
    }

    public List<NotificationResponse> getUserNotifications(String email) {
        return notificationRepository.findByUserEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<NotificationResponse> getUnreadNotifications(String email) {
        return notificationRepository.findByUserEmailAndReadFalseOrderByCreatedAtDesc(email)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public long getUnreadCount(String email) {
        return notificationRepository.countByUserEmailAndReadFalse(email);
    }

    @Transactional
    public void markAsRead(Long notificationId, String userEmail) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification introuvable"));

        if (!notification.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Accès non autorisé");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(String userEmail) {
        List<Notification> notifications = notificationRepository.findByUserEmailAndReadFalseOrderByCreatedAtDesc(userEmail);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getMessage(),
                notification.getType(),
                notification.isRead(),
                notification.getCreatedAt(),
                notification.getRelatedEntityType(),
                notification.getRelatedEntityId()
        );
    }
}
