package com.shop.service.tech.order_service.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "products")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Product {

    @Id
    String id;
    String name;
    String description;
    String category;
    String company;
    BigDecimal price;
    String priceType;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}