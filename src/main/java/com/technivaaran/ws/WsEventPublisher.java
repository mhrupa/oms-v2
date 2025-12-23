package com.technivaaran.ws;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WsEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public WsEventPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void customerChanged() {
        messagingTemplate.convertAndSend("/topic/customer", UiEvent.changed("CUSTOMER"));
    }

    public void customerCreated(String entity, String id) {
        messagingTemplate.convertAndSend("/topic/customer", UiEvent.upsert(entity, id));
    }

    public void customerAccountChanged() {
        messagingTemplate.convertAndSend("/topic/customerAccount", UiEvent.changed("CUSTOMER_ACCOUNT"));
    }

    public void vendorChanged() {
        messagingTemplate.convertAndSend("/topic/vendor", UiEvent.changed("VENDOR"));
    }

    public void paymentAccountChanged() {
        messagingTemplate.convertAndSend("/topic/payment", UiEvent.upsert("PAYMENT_ACCOUNT"));
    }

    public void notificationNew(String id) {
        messagingTemplate.convertAndSend("/topic/notifications",
                new UiEvent("NOTIFICATION_NEW", "NOTIFICATION", "NEW", id, System.currentTimeMillis()));
    }
}
