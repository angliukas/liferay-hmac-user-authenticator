package com.example.hmacauth.config;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.example.hmacauth.service.EmailService;

@Configuration
public class EmailConfig {

    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(getRequiredProperty("MAIL_HOST", "mail.host"));
        mailSender.setPort(getOptionalIntProperty("MAIL_PORT", "mail.port", 25));

        String username = getOptionalProperty("MAIL_USERNAME", "mail.username");
        String password = getOptionalProperty("MAIL_PASSWORD", "mail.password");
        if (username != null) {
            mailSender.setUsername(username);
        }
        if (password != null) {
            mailSender.setPassword(password);
        }

        Properties properties = mailSender.getJavaMailProperties();
        properties.setProperty("mail.transport.protocol", "smtp");
        properties.setProperty(
            "mail.smtp.auth",
            String.valueOf(getOptionalBooleanProperty("MAIL_SMTP_AUTH", "mail.smtp.auth", username != null))
        );
        properties.setProperty(
            "mail.smtp.starttls.enable",
            String.valueOf(getOptionalBooleanProperty("MAIL_SMTP_STARTTLS", "mail.smtp.starttls.enable", true))
        );
        properties.setProperty(
            "mail.smtp.ssl.enable",
            String.valueOf(getOptionalBooleanProperty("MAIL_SMTP_SSL", "mail.smtp.ssl.enable", false))
        );

        return mailSender;
    }

    @Bean
    public EmailService emailService(JavaMailSender mailSender) {
        String defaultFrom = getOptionalProperty("MAIL_FROM", "mail.from");
        return new EmailService(mailSender, defaultFrom);
    }

    private String getRequiredProperty(String envKey, String sysKey) {
        String value = getOptionalProperty(envKey, sysKey);
        if (value == null) {
            throw new IllegalStateException(
                "Missing required mail configuration for " + envKey + " or " + sysKey
            );
        }
        return value;
    }

    private String getOptionalProperty(String envKey, String sysKey) {
        String value = System.getProperty(sysKey);
        if (value == null || value.trim().isEmpty()) {
            value = System.getenv(envKey);
        }
        if (value != null && value.trim().isEmpty()) {
            return null;
        }
        return value;
    }

    private int getOptionalIntProperty(String envKey, String sysKey, int defaultValue) {
        String value = getOptionalProperty(envKey, sysKey);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            throw new IllegalStateException("Invalid integer for " + envKey + " or " + sysKey + ": " + value, ex);
        }
    }

    private boolean getOptionalBooleanProperty(String envKey, String sysKey, boolean defaultValue) {
        String value = getOptionalProperty(envKey, sysKey);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value.trim());
    }
}
