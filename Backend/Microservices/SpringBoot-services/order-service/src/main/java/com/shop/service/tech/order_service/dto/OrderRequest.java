package com.shop.service.tech.order_service.dto;

import java.math.BigDecimal;

public record OrderRequest 
(
    Long orderId,
    String customerName,
    String skuId,
    String deliveryDate,
    BigDecimal price,
    String orderDate,
    Integer quantity
){}
