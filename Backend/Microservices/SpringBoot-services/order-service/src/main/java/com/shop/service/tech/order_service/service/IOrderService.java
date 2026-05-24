package com.shop.service.tech.order_service.service;

import java.util.List;

import com.shop.service.tech.order_service.dto.OrderRequest;
import com.shop.service.tech.order_service.dto.OrderResponse;

public interface IOrderService 
{

    List<OrderResponse> getAllOrders();
    
    OrderResponse getOrderById(long orderId);
    
    OrderResponse createOrder(OrderRequest request);

    List<OrderResponse> createOrder(List<OrderRequest> request);

    OrderResponse updateOrder(long orderId, OrderRequest request);

    OrderResponse updateOrder(OrderRequest request);
    
    void deleteOrder(long orderId);
    
}
