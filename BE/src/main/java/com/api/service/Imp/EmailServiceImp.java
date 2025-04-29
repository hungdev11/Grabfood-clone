package com.api.service.Imp;

import com.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImp implements EmailService {

    private final JavaMailSender mailSender;

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
}
