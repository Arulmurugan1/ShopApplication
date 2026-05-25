package com.shop.service.tech.notification_service.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import com.shop.service.tech.notification_service.events.OrderPlacedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaNotificationService {

    private final JavaMailSender mailSender;
    
    @KafkaListener(topics = "order-events", groupId = "notification-group", containerFactory = "kafkaListenerContainerFactory")
    public void handleNotification(OrderPlacedEvent event) {
        
        log.info("Received OrderPlacedEvent: {}", event);

        MimeMessagePreparator messagePreparator = mimeMessage -> {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            helper.setFrom("shopApp@email.com");
            helper.setTo(event.customerName().toLowerCase() + "@email.com");
            helper.setSubject("Order Placed");
            helper.setText("""
                Dear %s,

                Your order with ID %d has been placed successfully on %s. Quantity: %d.
                Thank you for shopping with us!
                    """.formatted(  
                        event.customerName(),
                        event.orderId(),
                        event.orderDate(),
                        event.quantity()
                    ));
            
        };

        log.info("Sending email for Order ID: {}", event.orderId());

        try{
            mailSender.send(messagePreparator);
            log.info("Email sent successfully for Order ID: {}", event.orderId());
        }catch(Exception e){
            log.error("Failed to send email for Order ID: {}. Error: {}", event.orderId(), e.getMessage());
        }
        
        log.info("Finished processing OrderPlacedEvent for Order ID: {}", event.orderId());

    }
}
