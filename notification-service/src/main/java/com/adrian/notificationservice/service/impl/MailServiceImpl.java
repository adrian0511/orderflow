package com.adrian.notificationservice.service.impl;

import com.adrian.notificationservice.dto.request.ConfirmMailRequest;
import com.adrian.notificationservice.dto.request.FailedMailRequest;
import com.adrian.notificationservice.service.interf.IMailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailServiceImpl implements IMailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendConfirmMail(ConfirmMailRequest request) {
        try {

            Context cx = new Context();
            cx.setVariable("orderId", request.getOrderId());
            cx.setVariable("username", request.getUsername());
            cx.setVariable("totalAmount", request.getTotalAmount());
            cx.setVariable("date", request.getConfirmedAt());
            cx.setVariable("items", request.getItems());

            String htmlContent = templateEngine.process("payment-success", cx);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(request.getUserEmail());
            helper.setFrom("adriangarces0310@gmail.com");
            helper.setSubject("Pedido " + request.getOrderId() + " confirmado - Pago recibido");
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("Confirmation email sent successfully");

        } catch (MessagingException e) {
            return;
        }
    }

    @Override
    public void sendFailedMail(FailedMailRequest request) {
        try {

            Context cx = new Context();
            cx.setVariable("orderId", request.getOrderId());
            cx.setVariable("username", request.getUsername());
            cx.setVariable("amount", request.getTotalAmount());
            cx.setVariable("date", request.getFailedAt());
            cx.setVariable("reason", request.getErrorMessage());

            String htmlContent = templateEngine.process("payment-failed", cx);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(request.getUserEmail());
            helper.setFrom("adriangarces0310@gmail.com");
            helper.setSubject("No se pudo procesar el pago del pedido " + request.getOrderId());
            helper.setText(htmlContent, true);

            mailSender.send(message);

            log.info("Failure email sent successfully");

        } catch (MessagingException e) {
            return;
        }
    }

    @Override
    public void sendWelcomeMail(String username, String email) {
        try {
            int year = LocalDateTime.now().getYear();

            Context cx = new Context();
            cx.setVariable("username", username);
            cx.setVariable("year", year);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String html = templateEngine.process("welcome", cx);

            helper.setTo(email);
            helper.setFrom("adriangarces0310@gmail.com");
            helper.setSubject("¡Bienvenido a OrderFlow, " + username);
            helper.setText(html, true);

            mailSender.send(message);

            log.info("Welcome email sent successfully");

        } catch (MessagingException e) {
            return;
        }
    }
}
