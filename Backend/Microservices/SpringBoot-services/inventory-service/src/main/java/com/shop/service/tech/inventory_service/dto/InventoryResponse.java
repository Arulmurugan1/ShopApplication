package com.shop.service.tech.inventory_service.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse 
{
    String skuId;
    Integer quantity;   
    Integer reservedQuantity;
    Integer availableQuantity;
    LocalDateTime createdAt;
}
