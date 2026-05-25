package com.shop.service.tech.order_service.events;

public record OrderPlacedEvent 
(
    Long orderId,
    String customerName,
    String skuId,
    String orderDate,
    Integer quantity
){}
