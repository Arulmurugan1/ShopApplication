package com.shop.service.tech.order_service.client.feignClient.inevntory;

import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@HttpExchange
public interface IInventoryClient 
{
    @GetExchange("/api/inventory/check")
    @CircuitBreaker(name = "inventoryService", fallbackMethod = "checkInventoryFallback")
    @Retry(name = "inventoryService")
    public boolean checkInventory(@RequestParam("skuId") String skuId,@RequestParam("quantity") Integer quantity);

    default boolean checkInventoryFallback(String skuId, Integer quantity, Throwable throwable){
        
        LoggerFactory.getLogger(IInventoryClient.class).info("Inventory service is currently unavailable. Fallback method invoked: " + throwable.getMessage());

        // Assume inventory is not available when the service is down
        return false; 
    }
}
