package com.api.service.Imp;

import com.api.entity.Reminder;
import com.api.entity.User;
import com.api.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImp implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private String frontendUrl = "http://localhost:3000";

    @Override
    public void sendPasswordResetEmail(String to, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Password Reset Request");

        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        message.setText("To reset your password, click the link below:\n\n" + resetUrl +
                "\n\nIf you didn't request a password reset, please ignore this email.");

        mailSender.send(message);
        log.info("Password reset email sent to: {}", to);
    }

    @Override
    public void sendRestaurantAccountInfo(String to, String username, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Restaurant Account Information");
        message.setText("Your restaurant registration has been approved. Here are your account details:\n\n" +
                "Username: " + username + "\n" +
                "Password: " + password + "\n\n" +
                "You can login at: " + frontendUrl + "/login\n\n" +
                "Please change your password after the first login.");

        mailSender.send(message);
        log.info("Restaurant account information email sent to: {}", to);
    }
    @Async
    @Override
    public void sendReminderEmail(User user, Reminder reminder) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(user.getEmail());
            helper.setSubject("Reminder: " + reminder.getTitle());

            Context context = new Context();
            context.setVariable("name", user.getName());
            context.setVariable("title", reminder.getTitle());
            context.setVariable("description", reminder.getDescription());

            String emailContent = templateEngine.process("reminder-email", context);
            helper.setText(emailContent, true);

            mailSender.send(message);

            log.info("Reminder email sent to: {}", user.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send reminder email to {}: {}", user.getEmail(), e.getMessage());
        }
    }
}
