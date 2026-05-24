package com.shop.service.tech.inventory_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.shop.service.tech.inventory_service.dao.Inventory;

public interface IInventoryRepository extends JpaRepository<Inventory, String>
{
    boolean existsBySkuIdAndAvailableQuantityIsGreaterThanEqual(String skuId, int quantity);
}