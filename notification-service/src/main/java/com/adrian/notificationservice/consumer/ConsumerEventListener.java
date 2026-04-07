package com.adrian.notificationservice.consumer;

import com.adrian.notificationservice.dto.event.OrderCompletedEvent;
import com.adrian.notificationservice.dto.event.OrderFailedEvent;
import com.adrian.notificationservice.dto.event.UserCreatedEvent;
import com.adrian.notificationservice.dto.request.ConfirmMailRequest;
import com.adrian.notificationservice.dto.request.FailedMailRequest;
import com.adrian.notificationservice.dto.response.OrderItemResponse;
import com.adrian.notificationservice.service.interf.IMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

@Component
public class ConsumerEventListener {

    @Autowired
    private IMailService service;

    @KafkaListener(topics = "order.completed", groupId = "notification-group")
    public void handleOrderConfirm(OrderCompletedEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm:ss a");
        String format = formatter.format(event.getConfirmedAt());

        StringBuilder stringBuilder = new StringBuilder();
        for (OrderItemResponse itemResponse : event.getItems()) {
            stringBuilder.append(String.format(
                    "<tr><td>%s</td><td>%d</td><td>%.2f</td><td>%.2f</></tr>",
                    itemResponse.getProductName(), itemResponse.getQuantity(), itemResponse.getUnitPrice(),
                    itemResponse.getUnitPrice().multiply(BigDecimal.valueOf(itemResponse.getQuantity()))
            ));
        }

        ConfirmMailRequest request = new ConfirmMailRequest(
                event.getOrderId(),
                event.getUsername(),
                event.getUserEmail(),
                event.getTotalAmount(),
                stringBuilder.toString(),
                format
        );

        service.sendConfirmMail(request);
    }

    @KafkaListener(topics = "order.failed", groupId = "notification-group")
    public void handleOrderFailed(OrderFailedEvent event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy, h:mm:ss a");
        String format = formatter.format(event.getFailedAt());

        String errorMessage = "<p><span class=\"reason\">Motivo:</span> " + event.getErrorMessage() + "</p>";
        String amount = "<p><span class=\"reason\">Monto intentado:</span> " + event.getTotalAmount().toString() + " USD</p>";
        String date = "<p><span class=\"reason\">Fecha del intento:</span> " + format + "</p>";

        FailedMailRequest request = new FailedMailRequest(
                event.getOrderId(),
                event.getUsername(),
                event.getUserEmail(),
                amount,
                date,
                errorMessage
        );

        service.sendFailedMail(request);
    }

    @KafkaListener(topics = "user.created", groupId = "notification-group")
    public void handleUserCreated(UserCreatedEvent event) {
        service.sendWelcomeMail(event.getUsername(), event.getEmail());
    }
}
