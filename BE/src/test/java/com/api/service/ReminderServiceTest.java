package com.api.service;

import com.api.dto.request.ReminderRequest;
import com.api.dto.response.ReminderResponse;
import com.api.entity.Reminder;
import com.api.entity.User;
import com.api.exception.AppException;
import com.api.exception.ErrorCode;
import com.api.repository.ReminderRepository;
import com.api.repository.UserRepository;
import com.api.service.Imp.ReminderServiceImp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReminderServiceTest {

    @Mock
    private ReminderRepository reminderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    private ReminderServiceImp reminderService;

    @BeforeEach
    void setUp() {
        reminderService = new ReminderServiceImp(
                reminderRepository,
                userRepository,
                emailService
        );
    }

    @Test
    @DisplayName("Should create reminder successfully")
    void createReminder_Success() {
        // Arrange
        Long userId = 1L;
        ReminderRequest request = new ReminderRequest();
        request.setTitle("Test Reminder");
        request.setDescription("Remember to test");
        request.setReminderTime(LocalDateTime.now().plusHours(1));
        request.setEmailEnabled(true);

        User mockUser = new User();
        mockUser.setId(userId);
        mockUser.setEmail("test@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(reminderRepository.save(any(Reminder.class))).thenAnswer(invocation -> {
            Reminder reminder = invocation.getArgument(0);
            reminder.setId(1L);
            return reminder;
        });

        // Act
        ReminderResponse response = reminderService.createReminder(userId, request);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(request.getTitle(), response.getTitle());
        assertEquals(request.getDescription(), response.getDescription());
        assertEquals(request.getReminderTime(), response.getReminderTime());
        assertFalse(response.isProcessed());

        ArgumentCaptor<Reminder> reminderCaptor = ArgumentCaptor.forClass(Reminder.class);
        verify(reminderRepository).save(reminderCaptor.capture());

        Reminder savedReminder = reminderCaptor.getValue();
        assertEquals(request.getTitle(), savedReminder.getTitle());
        assertEquals(request.getDescription(), savedReminder.getDescription());
        assertEquals(request.getReminderTime(), savedReminder.getReminderTime());
        assertEquals(request.isEmailEnabled(), savedReminder.isEmailEnabled());
        assertEquals(mockUser, savedReminder.getUser());
    }

    @Test
    @DisplayName("Should throw exception when user not found during reminder creation")
    void createReminder_UserNotFound() {
        // Arrange
        Long userId = 999L;
        ReminderRequest request = new ReminderRequest();
        request.setTitle("Test Reminder");

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            reminderService.createReminder(userId, request);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(reminderRepository, never()).save(any(Reminder.class));
    }

    @Test
    @DisplayName("Should get user reminders successfully")
    void getUserReminders_Success() {
        // Arrange
        Long userId = 1L;
        List<Reminder> mockReminders = Arrays.asList(
                createMockReminder(1L, "Reminder 1", LocalDateTime.now().plusHours(1), false),
                createMockReminder(2L, "Reminder 2", LocalDateTime.now().plusHours(2), true)
        );

        when(reminderRepository.findByUserIdOrderByReminderTimeDesc(userId)).thenReturn(mockReminders);

        // Act
        List<ReminderResponse> responses = reminderService.getUserReminders(userId);

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());

        assertEquals(1L, responses.get(0).getId());
        assertEquals("Reminder 1", responses.get(0).getTitle());
        assertEquals(mockReminders.get(0).getReminderTime(), responses.get(0).getReminderTime());
        assertFalse(responses.get(0).isProcessed());

        assertEquals(2L, responses.get(1).getId());
        assertEquals("Reminder 2", responses.get(1).getTitle());
        assertEquals(mockReminders.get(1).getReminderTime(), responses.get(1).getReminderTime());
        assertTrue(responses.get(1).isProcessed());
    }

    @Test
    @DisplayName("Should delete reminder successfully")
    void deleteReminder_Success() {
        // Arrange
        Long reminderId = 1L;
        Reminder mockReminder = createMockReminder(reminderId, "Test Reminder", LocalDateTime.now(), false);

        when(reminderRepository.findById(reminderId)).thenReturn(Optional.of(mockReminder));

        // Act
        reminderService.deleteReminder(reminderId);

        // Assert
        verify(reminderRepository).delete(mockReminder);
    }

    @Test
    @DisplayName("Should throw exception when reminder not found during deletion")
    void deleteReminder_NotFound() {
        // Arrange
        Long reminderId = 999L;

        when(reminderRepository.findById(reminderId)).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            reminderService.deleteReminder(reminderId);
        });

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, exception.getErrorCode());
        verify(reminderRepository, never()).delete(any(Reminder.class));
    }

    @Test
    @DisplayName("Should delete all reminders for a user")
    void deleteAllReminders_Success() {
        // Arrange
        Long userId = 1L;
        User mockUser = new User();
        mockUser.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        reminderService.deleteAllReminders(userId);

        // Assert
        verify(reminderRepository).deleteByUserId(userId);
    }

    @Test
    @DisplayName("Should throw exception when user not found during delete all reminders")
    void deleteAllReminders_UserNotFound() {
        // Arrange
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        AppException exception = assertThrows(AppException.class, () -> {
            reminderService.deleteAllReminders(userId);
        });

        assertEquals(ErrorCode.USER_NOT_FOUND, exception.getErrorCode());
        verify(reminderRepository, never()).deleteByUserId(anyLong());
    }

    @Test
    @DisplayName("Should process due reminders")
    void processReminders_Success() {
        // Arrange
        List<Reminder> dueReminders = Arrays.asList(
                createMockReminderWithUser(1L, "Reminder 1", true),
                createMockReminderWithUser(2L, "Reminder 2", false)
        );

        when(reminderRepository.findDueReminders()).thenReturn(dueReminders);

        // Act
        reminderService.processReminders();

        // Assert
        verify(emailService).sendReminderEmail(eq(dueReminders.get(0).getUser()), eq(dueReminders.get(0)));
        verify(emailService, never()).sendReminderEmail(eq(dueReminders.get(1).getUser()), eq(dueReminders.get(1)));

        ArgumentCaptor<Reminder> reminderCaptor = ArgumentCaptor.forClass(Reminder.class);
        verify(reminderRepository, times(2)).save(reminderCaptor.capture());

        List<Reminder> capturedReminders = reminderCaptor.getAllValues();
        assertTrue(capturedReminders.get(0).isProcessed());
        assertTrue(capturedReminders.get(1).isProcessed());
    }

    @Test
    @DisplayName("Should handle exceptions during reminder processing")
    void processReminders_HandlesExceptions() {
        // Arrange
        Reminder mockReminder = createMockReminderWithUser(1L, "Reminder 1", true);
        when(reminderRepository.findDueReminders()).thenReturn(List.of(mockReminder));

        // Set up the mock to throw an exception when sendReminderEmail is called
        doThrow(new RuntimeException("Email sending failed")).when(emailService)
                .sendReminderEmail(any(User.class), any(Reminder.class));

        // Act - should not throw exception
        assertDoesNotThrow(() -> reminderService.processReminders());

        // Assert - should NOT expect save since the exception prevents it
        // The exception is caught, but the current implementation doesn't continue to save
        verify(reminderRepository, never()).save(any(Reminder.class));
    }

    // Helper methods to create mock entities
    private Reminder createMockReminder(Long id, String title, LocalDateTime reminderTime, boolean processed) {
        Reminder reminder = new Reminder();
        reminder.setId(id);
        reminder.setTitle(title);
        reminder.setDescription("Description for " + title);
        reminder.setReminderTime(reminderTime);
        reminder.setProcessed(processed);
        return reminder;
    }

    private Reminder createMockReminderWithUser(Long id, String title, boolean emailEnabled) {
        User user = new User();
        user.setId(id);
        user.setEmail("user" + id + "@example.com");

        Reminder reminder = createMockReminder(id, title, LocalDateTime.now().minusHours(1), false);
        reminder.setUser(user);
        reminder.setEmailEnabled(emailEnabled);

        return reminder;
    }
}