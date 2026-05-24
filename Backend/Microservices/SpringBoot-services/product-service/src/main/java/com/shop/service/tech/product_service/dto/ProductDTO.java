package com.shop.service.tech.product_service.dto;

import java.math.BigDecimal;

public record ProductDTO(
    String id,
    String name,
    String description,
    String category,
    String company,
    BigDecimal price,
    String priceType
) {

   
}
