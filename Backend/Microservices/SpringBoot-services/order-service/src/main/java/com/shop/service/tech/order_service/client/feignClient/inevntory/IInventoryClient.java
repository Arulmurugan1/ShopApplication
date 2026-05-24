package com.shop.service.tech.order_service.client.feignClient.inevntory;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface IInventoryClient 
{
    @GetExchange("/api/inventory//check")
    public boolean checkInventory(
            @RequestParam("skuId") String skuId,
            @RequestParam("quantity") Integer quantity);
}
