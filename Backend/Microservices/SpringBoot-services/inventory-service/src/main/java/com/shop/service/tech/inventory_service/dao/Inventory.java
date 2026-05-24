package com.shop.service.tech.inventory_service.dao;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "inventory")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inventory 
{
    @Id
    @Column(name = "sku_Id")
    String skuId;

    @Column(name = "quantity")
    int quantity;   
    
    @Column(name = "reserved_Quantity")
    int reservedQuantity;

    @Column(name = "available_Quantity")
    int availableQuantity;

    @Column(name = "created_At")
    LocalDateTime createdAt;
}