package com.shop.service.tech.order_service.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse 
{
    Long orderId;
    String customerName;
    String skuId; 
    String orderStatus;
    String deliveryDate;
    BigDecimal price;
    Integer quantity;
}
