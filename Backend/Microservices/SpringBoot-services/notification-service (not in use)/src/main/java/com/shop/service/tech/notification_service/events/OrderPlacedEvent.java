package com.shop.service.tech.notification_service.events;

public record OrderPlacedEvent 
(
    Long orderId,
    String customerName,
    String skuId,
    String orderDate,
    Integer quantity
){}
