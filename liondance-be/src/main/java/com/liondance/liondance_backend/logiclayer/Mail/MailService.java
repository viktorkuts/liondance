package com.liondance.liondance_backend.logiclayer.Mail;

public interface MailService {
    boolean sendMail(String to, String subject, String body);
}
