package com.propsightai.Service;

import com.propsightai.Service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Override
    public void sendVerificationEmail(String email, String token) {

        String link =
                frontendUrl +
                        "/verify-email?token=" +
                        token;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);

        message.setSubject("Verify your PropSight AI account");

        message.setText(
                "Welcome to PropSight AI.\n\n"
                        + "Please verify your email by clicking the link below:\n\n"
                        + link
                        + "\n\nThis link expires in 24 hours."
        );

        mailSender.send(message);
    }

    @Override
    public void sendPasswordResetEmail(String email, String token) {

        String link =
                frontendUrl +
                        "/reset-password?token=" +
                        token;

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setTo(email);

        message.setSubject("Reset Password");

        message.setText(
                "Click the link below to reset your password.\n\n"
                        + link
                        + "\n\nThis link expires in 30 minutes."
        );

        mailSender.send(message);
    }

}