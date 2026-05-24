package com.shop.service.tech.inventory_service.controller;

import org.springframework.web.bind.annotation.RestController;

import com.shop.service.tech.inventory_service.dto.ErrorResponse;
import com.shop.service.tech.inventory_service.dto.IneventoryRequest;
import com.shop.service.tech.inventory_service.dto.InventoryResponse;
import com.shop.service.tech.inventory_service.service.InventoryService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController 
{
    private final InventoryService inventoryService;    

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    public boolean checkInventory(@RequestParam String skuId, @RequestParam int quantity) 
    {
        return inventoryService.isInStock(skuId, quantity);
    }

    @PatchMapping("/reserve")
    public boolean reserveInventory(@RequestBody IneventoryRequest request) 
    {
        return inventoryService.reserveInventory(request.getSkuId(), request.getQuantity());
    }

    @GetMapping("/{skuId}")
    public InventoryResponse getInventoryBySkuId(@PathVariable String skuId) 
    {
        return inventoryService.getInventoryBySkuId(skuId);
    }

    @GetMapping
    public List<InventoryResponse> getAllInventories() 
    {
        return inventoryService.getAllInventories();
    }

    @PostMapping
    public InventoryResponse createInventory(@RequestBody InventoryResponse inventoryResponse) 
    {
        return inventoryService.createInventory(inventoryResponse);
    }

    @PutMapping
    public InventoryResponse updateInventory(@RequestBody InventoryResponse inventoryResponse) 
    {
        return inventoryService.updateInventory(inventoryResponse.getSkuId(), inventoryResponse);
    }

    @PatchMapping("/{skuId}/quantity")
    public InventoryResponse updateQuantity(@PathVariable String skuId, @RequestBody IneventoryRequest request) {
        return inventoryService.updateQuantity(skuId, request.getQuantity());
    }

    @DeleteMapping("/{skuId}")
    public boolean deleteInventory(@PathVariable String skuId) 
    {
        inventoryService.deleteInventory(skuId);
        return true;
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleException(Throwable ex) 
    {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(ex.getMessage()));
    }
}
