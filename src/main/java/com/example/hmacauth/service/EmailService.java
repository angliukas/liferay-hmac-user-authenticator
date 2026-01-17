package com.example.hmacauth.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String defaultFrom;

    public EmailService(JavaMailSender mailSender, String defaultFrom) {
        this.mailSender = mailSender;
        this.defaultFrom = defaultFrom;
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        sendSimpleMessage(to, subject, text, defaultFrom);
    }

    public void sendSimpleMessage(String to, String subject, String text, String from) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        if (from != null && !from.trim().isEmpty()) {
            message.setFrom(from);
        }
        mailSender.send(message);
    }
}
