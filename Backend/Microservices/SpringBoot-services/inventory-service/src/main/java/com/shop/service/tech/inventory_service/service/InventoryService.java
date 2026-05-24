package com.shop.service.tech.inventory_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.shop.service.tech.inventory_service.dao.Inventory;
import com.shop.service.tech.inventory_service.dto.InventoryResponse;
import com.shop.service.tech.inventory_service.repository.IInventoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryService 
{
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class); 
   
    private final IInventoryRepository inventoryRepository;

    public boolean isInStock(String skuId, int quantity) 
    {
        logger.info("Entering isInStock() method with skuId: {}, quantity: {}", skuId, quantity);
        try {
            logger.debug("Checking inventory availability for skuId: {} with quantity: {}", skuId, quantity);
            boolean inStock = inventoryRepository.existsBySkuIdAndAvailableQuantityIsGreaterThanEqual(skuId, quantity);
            logger.info("Inventory check completed for skuId: {} - in stock: {}", skuId, inStock);
            return inStock;
        } catch (Exception e) {
            logger.error("Error occurred in isInStock(skuId={}, quantity={}): {}", skuId, quantity, e.getMessage(), e);
            throw e;
        }
    }

    public List<InventoryResponse> getAllInventories() 
    {
        logger.info("Entering getAllInventories() method");
        try {
            logger.debug("Fetching all inventories from repository");
            List<Inventory> inventories = inventoryRepository.findAll();
            logger.debug("Found {} inventories, mapping to response objects", inventories.size());
            List<InventoryResponse> responses = inventories.stream()
                    .map(inv -> {
                        logger.debug("Mapping inventory for skuId: {}", inv.getSkuId());
                        return mapToInventoryResponse(inv);
                    })
                    .toList();
            logger.info("Successfully retrieved {} inventory records", responses.size());
            return responses;
        } catch (Exception e) {
            logger.error("Error occurred in getAllInventories(): {}", e.getMessage(), e);
            throw e;
        }
    }
    
    public InventoryResponse getInventoryBySkuId(String skuId) 
    {
        logger.info("Entering getInventoryBySkuId() method with skuId: {}", skuId);
        try {
            logger.debug("Searching for inventory with skuId: {}", skuId);
            Inventory inventory = inventoryRepository.findById(skuId)
                    .orElseThrow(() -> {
                        logger.warn("Inventory not found for skuId: {}", skuId);
                        return new RuntimeException("Inventory not found for SKU: " + skuId);
                    });
            logger.debug("Inventory found for skuId: {} with quantity: {}", skuId, inventory.getQuantity());
            InventoryResponse response = mapToInventoryResponse(inventory);
            logger.info("Successfully retrieved inventory for skuId: {}", skuId);
            return response;
        } catch (Exception e) {
            logger.error("Error occurred in getInventoryBySkuId(skuId={}): {}", skuId, e.getMessage(), e);
            throw e;
        }
    }

    public InventoryResponse createInventory(InventoryResponse inventoryResponse) 
    {
        logger.info("Entering createInventory() method");
        logger.debug("Inventory request received - skuId: {}, quantity: {}", inventoryResponse.getSkuId(), inventoryResponse.getQuantity());
        try {
            logger.debug("Validating inventory request");
            if(isValidRequest(inventoryResponse.getSkuId(), inventoryResponse.getQuantity()) == false)
            {
                logger.error("Invalid request - SKU: {}", inventoryResponse.getSkuId());
                throw new RuntimeException("Invalid SKU: " + inventoryResponse.getSkuId());
            }

            logger.debug("Checking if inventory already exists for skuId: {}", inventoryResponse.getSkuId());
            Inventory exisInventory = inventoryRepository.findById(inventoryResponse.getSkuId()).orElse(null);
            
            if(exisInventory != null)
            {
                logger.error("Inventory already exists for skuId: {}", inventoryResponse.getSkuId());
                throw new RuntimeException("Inventory already exists for SKU: " + inventoryResponse.getSkuId());
            }

            logger.debug("Creating new inventory object and mapping");
            Inventory inventory = mapToInventory(inventoryResponse);
            inventory.setCreatedAt(LocalDateTime.now());
            inventory.setAvailableQuantity(inventory.getQuantity());
            inventory.setReservedQuantity(0);
            logger.debug("Saving inventory with skuId: {}, quantity: {}", inventory.getSkuId(), inventory.getQuantity());

            Inventory savedInventory = inventoryRepository.save(inventory);
            logger.info("Inventory successfully created for skuId: {} with quantity: {}", savedInventory.getSkuId(), savedInventory.getQuantity());

            return mapToInventoryResponse(savedInventory);
        } catch (Exception e) {
            logger.error("Error occurred in createInventory(skuId={}): {}", inventoryResponse.getSkuId(), e.getMessage(), e);
            throw e;
        }
    }

    public InventoryResponse updateInventory(String skuId, InventoryResponse inventoryResponse) 
    {
        logger.info("Entering updateInventory() method with skuId: {}", skuId);
        Integer reqQtyObj = inventoryResponse.getQuantity();
        int reqQty = reqQtyObj == null ? 0 : reqQtyObj;
        Integer reqReservedObj = inventoryResponse.getReservedQuantity();
        int reqReserved = reqReservedObj == null ? 0 : reqReservedObj;
        logger.debug("Update request - new quantity: {}, reserved: {}", reqQty, reqReserved);
        try {
            logger.debug("Validating update request");
            if(isValidRequest(inventoryResponse.getSkuId(), reqQty) == false)
            {
                logger.error("Invalid update request for skuId: {}", inventoryResponse.getSkuId());
                throw new RuntimeException("Invalid SKU: " + inventoryResponse.getSkuId());
            }
            
            logger.debug("Fetching existing inventory for skuId: {}", skuId);
            Inventory existingInventory = inventoryRepository.findById(skuId)
                    .orElseThrow(() -> {
                        logger.warn("Inventory not found for skuId: {}", skuId);
                        return new RuntimeException("Inventory not found for SKU: " + skuId);
                    });

            logger.debug("Calculating new quantity: {} + {}", reqQty, existingInventory.getQuantity());
            int newQuantity = reqQty + existingInventory.getQuantity();
            existingInventory.setQuantity(newQuantity);
            existingInventory.setCreatedAt(LocalDateTime.now());
            logger.debug("Calculating new reserved quantity: {} + {}", reqReserved, existingInventory.getReservedQuantity());
            int newReservedQuantity = reqReserved + existingInventory.getReservedQuantity();
            existingInventory.setReservedQuantity(newReservedQuantity);

            int newAvailableQuantity = newQuantity - newReservedQuantity;
            logger.debug("New available quantity calculated: {}", newAvailableQuantity);

            if(newAvailableQuantity <= -1)
            {
                logger.error("Insufficient inventory available for skuId: {}, available: {}", skuId, newAvailableQuantity);
                throw new RuntimeException("No Inventory available for SKU: " + skuId);
            }
            else
            {
                existingInventory.setAvailableQuantity(newAvailableQuantity);
            }

            logger.debug("Saving updated inventory for skuId: {}", skuId);
            Inventory updatedInventory = inventoryRepository.save(existingInventory);
            logger.info("Inventory successfully updated for skuId: {}, new total quantity: {}", skuId, updatedInventory.getQuantity());
            return mapToInventoryResponse(updatedInventory);
        } catch (Exception e) {
            logger.error("Error occurred in updateInventory(skuId={}): {}", skuId, e.getMessage(), e);
            throw e;
        }
    }

    public void deleteInventory(String skuId) 
    {
        logger.info("Entering deleteInventory() method with skuId: {}", skuId);
        try {
            logger.debug("Validating skuId: {}", skuId);
            if(isValidSkuId(skuId) == false)
            {
                logger.error("Invalid SKU provided: {}", skuId);
                throw new RuntimeException("Invalid SKU: " + skuId);
            }

            logger.debug("Fetching inventory for deletion with skuId: {}", skuId);
            Inventory existingInventory = inventoryRepository.findById(skuId)
                    .orElseThrow(() -> {
                        logger.warn("Inventory not found for deletion with skuId: {}", skuId);
                        return new RuntimeException("Inventory not found for SKU: " + skuId);
                    });
            logger.debug("Deleting inventory for skuId: {}", skuId);
            inventoryRepository.delete(existingInventory);
            logger.info("Inventory successfully deleted for skuId: {}", skuId);
        } catch (Exception e) {
            logger.error("Error occurred in deleteInventory(skuId={}): {}", skuId, e.getMessage(), e);
            throw e;
        }
    }

    public InventoryResponse updateQuantity(String skuId, Integer quantityToAdd) {
        logger.info("Entering updateQuantity() method with skuId: {}, quantityToAdd: {}", skuId, quantityToAdd);
        try {
            if (isValidSkuId(skuId) == false) {
                logger.error("Invalid SKU provided for updateQuantity: {}", skuId);
                throw new RuntimeException("Invalid SKU: " + skuId);
            }

            int qtyToAdd = quantityToAdd == null ? 0 : quantityToAdd;
            if (isValidQuantity(qtyToAdd) == false) {
                logger.error("Invalid quantity provided for updateQuantity: {}", qtyToAdd);
                throw new RuntimeException("Invalid quantity: " + qtyToAdd);
            }

            Inventory existingInventory = inventoryRepository.findById(skuId)
                    .orElseThrow(() -> {
                        logger.warn("Inventory not found for skuId: {}", skuId);
                        return new RuntimeException("Inventory not found for SKU: " + skuId);
                    });

            logger.debug("Current inventory - total: {}, available: {}, reserved: {}", existingInventory.getQuantity(), existingInventory.getAvailableQuantity(), existingInventory.getReservedQuantity());

            int newQuantity = existingInventory.getQuantity() + qtyToAdd;
            existingInventory.setQuantity(newQuantity);
            existingInventory.setAvailableQuantity(existingInventory.getAvailableQuantity() + qtyToAdd);
            existingInventory.setCreatedAt(LocalDateTime.now());

            Inventory updated = inventoryRepository.save(existingInventory);
            logger.info("Quantity updated for skuId: {}, new total: {}", skuId, updated.getQuantity());
            return mapToInventoryResponse(updated);
        } catch (Exception e) {
            logger.error("Error occurred in updateQuantity(skuId={}, qtyToAdd={}): {}", skuId, quantityToAdd, e.getMessage(), e);
            throw e;
        }
    }

    private boolean isValidSkuId(String skuId) 
    {
        return skuId != null && !skuId.trim().isEmpty();
    }

    private boolean isValidQuantity(int quantity) 
    {
        return quantity >= 0;
    }

    private boolean isValidRequest(String skuId, int quantity) 
    {
        return isValidSkuId(skuId) && isValidQuantity(quantity);
    }

    private Inventory mapToInventory(InventoryResponse inventoryResponse) 
    {
        return new Inventory(
                inventoryResponse.getSkuId(),
                inventoryResponse.getQuantity() == null ? 0 : inventoryResponse.getQuantity(),
                inventoryResponse.getReservedQuantity() == null ? 0 : inventoryResponse.getReservedQuantity(),
                inventoryResponse.getAvailableQuantity() == null ? 0 : inventoryResponse.getAvailableQuantity(),
                LocalDateTime.now()
        );
    }

    private InventoryResponse mapToInventoryResponse(Inventory inventory) 
    {
        return new InventoryResponse(
                inventory.getSkuId(), 
                inventory.getQuantity(),
                inventory.getReservedQuantity(),
                inventory.getAvailableQuantity(),
                inventory.getCreatedAt()
        );
    }

    public boolean reserveInventory(String skuId, Integer quantity) 
    {
        logger.info("Entering reserveInventory() method with skuId: {}, quantity: {}", skuId, quantity);
        try {
            logger.debug("Fetching inventory for skuId: {}", skuId);
            Inventory inventory = inventoryRepository.findById(skuId)
                    .orElseThrow(() -> {
                        logger.warn("Inventory not found for reservation with skuId: {}", skuId);
                        return new RuntimeException("Inventory not found for SKU: " + skuId);
                    });

            logger.debug("Checking available inventory - available: {}, requested: {}", inventory.getAvailableQuantity(), quantity);
            if (inventory.getAvailableQuantity() < quantity) 
            {
                logger.error("Insufficient inventory for skuId: {}, available: {}, requested: {}", skuId, inventory.getAvailableQuantity(), quantity);
                throw new RuntimeException("Not enough inventory available for SKU: " + skuId);
            }

            logger.debug("Updating inventory - reducing available by {}, increasing reserved by {}", quantity, quantity);
            inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
            inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);
            inventoryRepository.save(inventory);
            logger.info("Inventory successfully reserved for skuId: {}, quantity: {}, new available: {}", skuId, quantity, inventory.getAvailableQuantity());

            return true;
        } catch (Exception e) {
            logger.error("Error occurred in reserveInventory(skuId={}, quantity={}): {}", skuId, quantity, e.getMessage(), e);
            throw e;
        }
    }
}
