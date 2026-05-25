package com.shop.service.tech.order_service.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.shop.service.tech.order_service.events.OrderPlacedEvent;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate;

    public void send(String message, OrderPlacedEvent event) {
        kafkaTemplate.send(message, event);
    }
} 