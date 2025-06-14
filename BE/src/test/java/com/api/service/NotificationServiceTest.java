package com.api.service;

import com.api.dto.response.NotificationResponse;
import com.api.entity.Account;
import com.api.entity.AccountNotification;
import com.api.entity.Notification;
import com.api.repository.AccountNotificationRepository;
import com.api.service.Imp.NotificationServiceImp;
import com.api.service.strategy.NotificationStrategy;
import com.api.utils.NotificationType;
import com.api.utils.TimeUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private List<NotificationStrategy> strategies;

    @Mock
    private AccountNotificationRepository accountNotificationRepository;

    @Mock
    private NotificationStrategy notificationStrategy;

    @InjectMocks
    private NotificationServiceImp notificationService;

    private Account testAccount;
    private AccountNotification testAccountNotification;
    private Notification testNotification;

    @BeforeEach
    void setUp() {
        testAccount = new Account();
        testAccount.setId(1L);

        testNotification = new Notification();
        testNotification.setId(1L);
        testNotification.setSubject("Test Subject");
        testNotification.setBody("Test Body");
        testNotification.setDate(LocalDateTime.now());

        testAccountNotification = new AccountNotification();
        testAccountNotification.setId(1L);
        testAccountNotification.setReceivedAccount(testAccount);
        testAccountNotification.setNotification(testNotification);
        testAccountNotification.setRead(false);
        testAccountNotification.setDeleted(false);
    }

    @Test
    void createNewNotification_WithSupportedType_ShouldReturnNotificationId() {
        // Given
        NotificationType type = NotificationType.NEW_ORDER;
        String subject = "Test Subject";
        String body = "Test Body";
        long expectedId = 123L;

        when(strategies.stream()).thenReturn(Arrays.asList(notificationStrategy).stream());
        when(notificationStrategy.supports(type)).thenReturn(true);
        when(notificationStrategy.create(testAccount, subject, body)).thenReturn(expectedId);

        // When
        long result = notificationService.createNewNotification(testAccount, subject, body, type);

        // Then
        assertEquals(expectedId, result);
        verify(notificationStrategy).supports(type);
        verify(notificationStrategy).create(testAccount, subject, body);
    }

    @Test
    void createNewNotification_WithUnsupportedType_ShouldReturnMinusOne() {
        // Given
        NotificationType type = NotificationType.NEW_ORDER;
        String subject = "Test Subject";
        String body = "Test Body";

        when(strategies.stream()).thenReturn(Arrays.asList(notificationStrategy).stream());
        when(notificationStrategy.supports(type)).thenReturn(false);

        // When
        long result = notificationService.createNewNotification(testAccount, subject, body, type);

        // Then
        assertEquals(-1L, result);
        verify(notificationStrategy).supports(type);
        verify(notificationStrategy, never()).create(any(), any(), any());
    }

    @Test
    void sendNewOrderNotification_ShouldSendMessageToRestaurant() {
        // Given
        long restaurantId = 123L;

        // When
        notificationService.sendNewOrderNotification(restaurantId);

        // Then
        verify(messagingTemplate).convertAndSend("/topic/restaurant/" + restaurantId, "");
    }

    @Test
    void sendUserNotificationWhenOrderStatusChanged_ShouldSendMessageToUser() {
        // Given
        long userId = 456L;

        // When
        notificationService.sendUserNotificationWhenOrderStatusChanged(userId);

        // Then
        verify(messagingTemplate).convertAndSend("/topic/client/" + userId, "");
    }

    @Test
    void sendDeliveryGuyNotificationWhenOrderStatusChanged_ShouldSendMessageToDeliveryGuy() {
        // Given
        long deliveryGuyId = 789L;

        // When
        notificationService.sendDeliveryGuyNotificationWhenOrderStatusChanged(deliveryGuyId);

        // Then
        verify(messagingTemplate).convertAndSend("/topic/ship/" + deliveryGuyId, "");
    }

    @Test
    void sendAdminNotification_ShouldSendMessageToAdmin() {
        // Given
        long userId = 101L;

        // When
        notificationService.sendAdminNotification(userId);

        // Then
        verify(messagingTemplate).convertAndSend("/topic/admin/" + userId, "");
    }

    @Test
    void markAsRead_WithExistingNotification_ShouldMarkAsRead() {
        // Given
        long notificationId = 1L;
        when(accountNotificationRepository.findById(notificationId))
                .thenReturn(Optional.of(testAccountNotification));

        // When
        notificationService.markAsRead(notificationId);

        // Then
        assertTrue(testAccountNotification.isRead());
        verify(accountNotificationRepository).save(testAccountNotification);
    }

    @Test
    void markAsRead_WithNonExistingNotification_ShouldDoNothing() {
        // Given
        long notificationId = 999L;
        when(accountNotificationRepository.findById(notificationId))
                .thenReturn(Optional.empty());

        // When
        notificationService.markAsRead(notificationId);

        // Then
        verify(accountNotificationRepository, never()).save(any());
    }

    @Test
    void markDeleted_WithExistingNotification_ShouldMarkAsDeletedAndRead() {
        // Given
        long notificationId = 1L;
        when(accountNotificationRepository.findById(notificationId))
                .thenReturn(Optional.of(testAccountNotification));

        // When
        notificationService.markDeleted(notificationId);

        // Then
        assertTrue(testAccountNotification.isRead());
        assertTrue(testAccountNotification.isDeleted());
        verify(accountNotificationRepository).save(testAccountNotification);
    }

    @Test
    void markAllAsRead_ShouldMarkAllUnreadNotificationsAsRead() {
        // Given
        AccountNotification unreadNotification1 = new AccountNotification();
        unreadNotification1.setRead(false);
        unreadNotification1.setDeleted(false);

        AccountNotification unreadNotification2 = new AccountNotification();
        unreadNotification2.setRead(false);
        unreadNotification2.setDeleted(false);

        AccountNotification readNotification = new AccountNotification();
        readNotification.setRead(true);
        readNotification.setDeleted(false);

        testAccount.setNotificationDetails(Arrays.asList(unreadNotification1, unreadNotification2, readNotification));

        // When
        notificationService.markAllAsRead(testAccount);

        // Then
        assertTrue(unreadNotification1.isRead());
        assertTrue(unreadNotification2.isRead());
//        verify(accountNotificationRepository).saveAll(argThat(list ->
//                list.size() == 2 && list.contains(unreadNotification1) && list.contains(unreadNotification2)
//        ));
    }

    @Test
    void markAllAsDeleted_ShouldMarkAllUndeletedNotificationsAsDeleted() {
        // Given
        AccountNotification notification1 = new AccountNotification();
        notification1.setDeleted(false);

        AccountNotification notification2 = new AccountNotification();
        notification2.setDeleted(false);

        AccountNotification deletedNotification = new AccountNotification();
        deletedNotification.setDeleted(true);

        testAccount.setNotificationDetails(Arrays.asList(notification1, notification2, deletedNotification));

        // When
        notificationService.markAllAsDeleted(testAccount);

        // Then
        assertTrue(notification1.isDeleted());
        assertTrue(notification1.isRead());
        assertTrue(notification2.isDeleted());
        assertTrue(notification2.isRead());
//        verify(accountNotificationRepository).saveAll(argThat(list ->
//                list.size() == 2 && list.contains(notification1) && list.contains(notification2)
//        ));
    }

    @Test
    void fetchNotificationsPopup_ShouldReturnFormattedNotifications() {
        // Given
        AccountNotification notification1 = new AccountNotification();
        notification1.setId(1L);
        notification1.setRead(false);
        notification1.setDeleted(false);

        Notification notif1 = new Notification();
        notif1.setSubject("Subject 1");
        notif1.setBody("Body 1");
        notif1.setDate(LocalDateTime.now().minusHours(1));
        notification1.setNotification(notif1);

        AccountNotification notification2 = new AccountNotification();
        notification2.setId(2L);
        notification2.setRead(true);
        notification2.setDeleted(false);

        Notification notif2 = new Notification();
        notif2.setSubject("Subject 2");
        notif2.setBody("Body 2");
        notif2.setDate(LocalDateTime.now().minusHours(2));
        notification2.setNotification(notif2);

        AccountNotification deletedNotification = new AccountNotification();
        deletedNotification.setDeleted(true);

        testAccount.setNotificationDetails(Arrays.asList(notification1, notification2, deletedNotification));

        try (MockedStatic<TimeUtil> timeUtilMock = mockStatic(TimeUtil.class)) {
            timeUtilMock.when(() -> TimeUtil.formatRelativeTime(any())).thenReturn("1 giờ");

            // When
            List<NotificationResponse> result = notificationService.fetchNotificationsPopup(testAccount);

            // Then
            assertEquals(2, result.size());
            assertEquals("Subject 1", result.get(0).getSubject());
            assertEquals("Body 1", result.get(0).getBody());
            assertFalse(result.get(0).isRead());
            assertEquals("1 giờ trước", result.get(0).getTimeArrived());
        }
    }
}
