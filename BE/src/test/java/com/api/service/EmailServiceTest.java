package com.api.service;

import com.api.entity.Reminder;
import com.api.entity.User;
import com.api.service.Imp.EmailServiceImp;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailServiceImp emailService;

    @Test
    void sendPasswordResetEmail_Success() {
        // Arrange
        String testEmail = "test@example.com";
        String testToken = "reset-token-123";

        // Act
        emailService.sendPasswordResetEmail(testEmail, testToken);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(testEmail, sentMessage.getTo()[0]);
        assertEquals("Password Reset Request", sentMessage.getSubject());
        assertTrue(sentMessage.getText().contains("reset-token-123"));
        assertTrue(sentMessage.getText().contains("reset your password"));
    }

    @Test
    void sendRestaurantAccountInfo_Success() {
        // Arrange
        String testEmail = "restaurant@example.com";
        String testUsername = "restaurant1";
        String testPassword = "password123";

        // Act
        emailService.sendRestaurantAccountInfo(testEmail, testUsername, testPassword);

        // Assert
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertEquals(testEmail, sentMessage.getTo()[0]);
        assertEquals("Restaurant Account Information", sentMessage.getSubject());

        String messageText = sentMessage.getText();
        assertTrue(messageText.contains(testUsername));
        assertTrue(messageText.contains(testPassword));
        assertTrue(messageText.contains("restaurant registration has been approved"));
        assertTrue(messageText.contains("change your password"));
    }

    @Test
    void sendReminderEmail_Success() throws Exception {
        // Arrange
        User testUser = new User();
        testUser.setEmail("user@example.com");
        testUser.setName("Test User");

        Reminder testReminder = new Reminder();
        testReminder.setTitle("Test Reminder");
        testReminder.setDescription("This is a test reminder description");

        // Mock MimeMessage creation
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Mock template processing
        when(templateEngine.process(eq("reminder-email"), any(Context.class)))
                .thenReturn("<html><body>Reminder Email Content</body></html>");

        // Act
        emailService.sendReminderEmail(testUser, testReminder);

        // Assert
        verify(mailSender).createMimeMessage();
        verify(templateEngine).process(eq("reminder-email"), any(Context.class));
        verify(mailSender).send(same(mimeMessage));

        // Verify context variables passed to template
        ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        verify(templateEngine).process(eq("reminder-email"), contextCaptor.capture());

        Context capturedContext = contextCaptor.getValue();
        assertEquals("Test User", capturedContext.getVariable("name"));
        assertEquals("Test Reminder", capturedContext.getVariable("title"));
        assertEquals("This is a test reminder description", capturedContext.getVariable("description"));
    }
}