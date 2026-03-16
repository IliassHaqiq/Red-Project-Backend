package com.example.ecommerce.unit.service;

import com.example.ecommerce.dto.NotificationResponse;
import com.example.ecommerce.entity.Notification;
import com.example.ecommerce.entity.RoleName;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.NotificationRepository;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void createNotification_shouldPersistNotificationForUser() {
        User user = User.builder().id(1L).email("client@shop.com").build();
        when(userRepository.findByEmail("client@shop.com")).thenReturn(Optional.of(user));

        notificationService.createNotification(
                "client@shop.com",
                "Message",
                "TYPE",
                "ORDER",
                1L
        );

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void createNotification_shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("unknown@shop.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                notificationService.createNotification("unknown@shop.com", "msg", "TYPE", "ORDER", 1L)
        );
    }

    @Test
    void notifyAllAdmins_shouldCreateNotificationForEachAdmin() {
        User admin1 = User.builder().id(1L).email("admin1@shop.com").build();
        User admin2 = User.builder().id(2L).email("admin2@shop.com").build();

        when(userRepository.findByRoleName(RoleName.ROLE_ADMIN)).thenReturn(List.of(admin1, admin2));

        notificationService.notifyAllAdmins("msg", "TYPE", "ORDER", 10L);

        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void getUserNotifications_shouldMapToResponseDtos() {
        Notification n = Notification.builder()
                .id(1L)
                .message("msg")
                .type("TYPE")
                .read(false)
                .relatedEntityType("ORDER")
                .relatedEntityId(10L)
                .build();

        when(notificationRepository.findByUserEmailOrderByCreatedAtDesc("client@shop.com"))
                .thenReturn(List.of(n));

        List<NotificationResponse> result = notificationService.getUserNotifications("client@shop.com");

        assertEquals(1, result.size());
        assertEquals("msg", result.get(0).message());
    }

    @Test
    void getUnreadCount_shouldDelegateToRepository() {
        when(notificationRepository.countByUserEmailAndReadFalse("client@shop.com")).thenReturn(5L);

        long count = notificationService.getUnreadCount("client@shop.com");

        assertEquals(5L, count);
    }
}

