package com.liondance.liondance_backend.logiclayer.Mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MailServiceImpl implements MailService {
    private final MailSender mailSender;
    private final SimpleMailMessage templateMessage;

    public MailServiceImpl(MailSender mailSender, @Value("${MAIL_SMTP_USER}@${SERVER_ROOT_DOMAIN}") String from) {
        this.mailSender = mailSender;
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        this.templateMessage = msg;
    }

    public boolean sendMail(String to, String subject, String body) {
        SimpleMailMessage msg = new SimpleMailMessage(this.templateMessage);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);
        try{
            mailSender.send(msg);
            log.info("Email sent to {}", to);
            return true;
        } catch (MailException e) {
            log.warn("Failed to send email to {}", to, e);
            return false;
        }
    }
}
