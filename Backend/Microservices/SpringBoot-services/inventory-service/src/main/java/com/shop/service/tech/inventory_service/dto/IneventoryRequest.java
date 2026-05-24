package com.shop.service.tech.inventory_service.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class IneventoryRequest 
{
    private String skuId;
    private Integer quantity;    
}
