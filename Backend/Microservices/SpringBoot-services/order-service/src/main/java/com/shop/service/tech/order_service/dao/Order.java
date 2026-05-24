package com.shop.service.tech.order_service.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
public class Order 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    
    @Column(name = "customer_name")
    String customerName;
    
    @Column(name = "sku_id")
    String skuId; 
    
    @Column(name = "order_status")
    String orderStatus;

    @Column(name = "order_date")
    LocalDateTime orderDate;

    @Column(name = "delivery_date")
    LocalDateTime deliveryDate;
    
    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;
    
    @Column(name = "price")
    BigDecimal price;

    @Column(name = "quantity")
    int quantity;
}
