package com.shop.service.tech.order_service.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.service.tech.order_service.client.feignClient.inevntory.IInventoryClient;
import com.shop.service.tech.order_service.dao.Order;
import com.shop.service.tech.order_service.dto.OrderRequest;
import com.shop.service.tech.order_service.dto.OrderResponse;
import com.shop.service.tech.order_service.repositoriy.IOrderRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService implements IOrderService
{
    @Autowired
    private IOrderRepository orderRepository;

    @Autowired
    private IInventoryClient invCLient;
    
    @Override
    public List<OrderResponse> getAllOrders() 
    {
        log.info("Entering getAllOrders() method");
        try {
            log.debug("Fetching all orders from repository");
            List<Order> orders = orderRepository.findAll();
            log.debug("Found {} orders, mapping to response objects", orders.size());
            List<OrderResponse> responses = orders.stream()
                    .map(order -> {
                        log.debug("Mapping order with id: {}", order.getId());
                        return mapToOrderResponse(order);
                    })
                    .toList();
            log.info("Successfully retrieved {} orders", responses.size());
            return responses;
        } catch (Exception e) {
            log.error("Error occurred in getAllOrders(): {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public OrderResponse getOrderById(long orderId) 
    {
        log.info("Entering getOrderById() method with orderId: {}", orderId);
        try {
            log.debug("Searching for order with id: {}", orderId);
            Order order = orderRepository.findById(orderId).orElseThrow(() -> {
                log.warn("Order not found with id: {}", orderId);
                return new RuntimeException("Order not found with id: " + orderId);
            });
            log.debug("Order found with id: {}, customer: {}, sku: {}", orderId, order.getCustomerName(), order.getSkuId());
            OrderResponse response = mapToOrderResponse(order);
            log.info("Successfully retrieved order with id: {}", orderId);
            return response;
        } catch (Exception e) {
            log.error("Error occurred in getOrderById(orderId={}): {}", orderId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public OrderResponse createOrder(OrderRequest orderRequest) 
    {
        log.info("Entering createOrder() method");
        log.debug("Order request received - customer: {}, skuId: {}, quantity: {}, price: {}", 
            orderRequest.customerName(), orderRequest.skuId(), orderRequest.quantity(), orderRequest.price());
        try {
            log.debug("Checking inventory availability for skuId: {} with quantity: {}", orderRequest.skuId(), orderRequest.quantity());
            boolean isInventoryAvailable = invCLient.checkInventory(orderRequest.skuId(), orderRequest.quantity());

            if(!isInventoryAvailable) {
                log.error("Inventory not available for skuId: {} with quantity: {}", orderRequest.skuId(), orderRequest.quantity());
                throw new RuntimeException("Inventory not available for SKU: " + orderRequest.skuId() + " with quantity: " + orderRequest.quantity());
            }

            log.debug("Creating order object from request");
            Order newOrder = mapToOrderCreateRequest(orderRequest);
            log.debug("Saving order to repository");
            Order savedOrder = orderRepository.save(newOrder);
            log.info("Order successfully created with id: {}, customer: {}, skuId: {}", savedOrder.getId(), savedOrder.getCustomerName(), savedOrder.getSkuId());
            return mapToOrderResponse(savedOrder);
        } catch (Exception e) {
            log.error("Error occurred in createOrder(skuId={}): {}", orderRequest.skuId(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<OrderResponse> createOrder(List<OrderRequest> request) 
    {
        log.info("Entering createOrder() method with batch size: {}", request.size());
        try {
            log.debug("Converting {} order requests to order objects", request.size());
            List<Order> orders = request.stream()
                    .map(req -> {
                        log.debug("Mapping order request for customer: {}, skuId: {}", req.customerName(), req.skuId());
                        return mapToOrderCreateRequest(req);
                    })
                    .toList();
            log.debug("Saving {} orders to repository in batch", orders.size());
            List<Order> savedOrders = orderRepository.saveAll(orders);
            log.debug("Converting saved orders to response objects");
            List<OrderResponse> responses = savedOrders.stream()
                    .map(order -> {
                        log.debug("Mapping saved order with id: {}", order.getId());
                        return mapToOrderResponse(order);
                    })
                    .toList();
            log.info("Successfully created {} orders in batch", responses.size());
            return responses;
        } catch (Exception e) {
            log.error("Error occurred in createOrder(batch size={}): {}", request.size(), e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public OrderResponse updateOrder(long orderId, OrderRequest orderRequest)
    {
        log.info("Entering updateOrder() method with orderId: {}", orderId);
        log.debug("Update request - customer: {}, skuId: {}, quantity: {}", orderRequest.customerName(), orderRequest.skuId(), orderRequest.quantity());
        try {
            log.debug("Fetching existing order with id: {}", orderId);
            Order existingOrder = orderRepository.findById(orderId).orElseThrow(() -> {
                log.warn("Order not found for update with id: {}", orderId);
                return new RuntimeException("Order not found with id: " + orderId);
            });
            log.debug("Order found, mapping update request");
            Order updatedOrder = mapToOrderUpdateRequest(orderRequest);
            updatedOrder.setId(existingOrder.getId());
            log.debug("Saving updated order with id: {}", orderId);
            Order savedOrder = orderRepository.save(updatedOrder);
            log.info("Order successfully updated with id: {}, new customer: {}", savedOrder.getId(), savedOrder.getCustomerName());
            return mapToOrderResponse(savedOrder);
        } catch (Exception e) {
            log.error("Error occurred in updateOrder(orderId={}): {}", orderId, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public OrderResponse updateOrder(OrderRequest orderRequest) 
    {
        log.info("Entering updateOrder() overload method with orderId: {}", orderRequest.orderId());
        log.debug("Delegating to updateOrder(long, OrderRequest) with orderId: {}", orderRequest.orderId());
        return updateOrder(orderRequest.orderId(), orderRequest);
    }

    @Override
    public void deleteOrder(long orderId) 
    {
        log.info("Entering deleteOrder() method with orderId: {}", orderId);
        try {
            log.debug("Checking if order exists with id: {}", orderId);
            if(!orderRepository.existsById(orderId)) {
                log.warn("Order not found for deletion with id: {}", orderId);
                throw new RuntimeException("Order not found with id: " + orderId);
            }
            log.debug("Deleting order with id: {}", orderId);
            orderRepository.deleteById(orderId);
            log.info("Order successfully deleted with id: {}", orderId);
        } catch (Exception e) {
            log.error("Error occurred in deleteOrder(orderId={}): {}", orderId, e.getMessage(), e);
            throw e;
        }
    }

    private OrderResponse mapToOrderResponse(Order savedOrder) 
    {
        if(savedOrder == null) throw new RuntimeException("No Order found");
        
        return new OrderResponse(
            savedOrder.getId(),
            savedOrder.getCustomerName(),
            savedOrder.getSkuId(),
            savedOrder.getOrderStatus(),
            parseDateToStrign(savedOrder.getDeliveryDate(), false),
            savedOrder.getPrice(),
            savedOrder.getQuantity()
        );
    }

    private Order mapToOrderWithRequestOrder(OrderRequest orderRequest,String orderStatus) 
    {
        if(orderRequest == null) throw new RuntimeException("Order request is null or empty");
        
        Order order = new Order();
        order.setCustomerName(orderRequest.customerName());
        order.setSkuId(orderRequest.skuId());
        order.setOrderStatus(orderStatus);
        order.setPrice(orderRequest.price());
        order.setQuantity(orderRequest.quantity()); 
        order.setOrderDate(parseDate(orderRequest.orderDate(), true));

        return order;
    }

    private Order mapToOrderUpdateRequest(OrderRequest orderRequest) 
    {
        if(orderRequest == null) throw new RuntimeException("Order request is null or empty");

        Order order = mapToOrderWithRequestOrder(orderRequest, "CREATED/UPDATED");
        order.setUpdatedAt(LocalDateTime.now());

        return order;
    }

    private Order mapToOrderCreateRequest(OrderRequest request) 
    {
        if(request == null) throw new RuntimeException("Order request is null or empty");
        Order order = mapToOrderWithRequestOrder(request, "CREATED");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        order.setOrderDate(parseDate(request.orderDate(), true));

        return order;
    }

    private LocalDateTime parseDate(String dateStr,boolean activeDateNeeded) 
    {
        if(dateStr == null || dateStr.isBlank()) return activeDateNeeded ? LocalDateTime.now() : null;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return LocalDateTime.parse(dateStr, formatter);
    }

    private String parseDateToStrign(LocalDateTime date, boolean activeDateNeeded) 
    {
        if(date == null) return activeDateNeeded ? LocalDateTime.now().toString() : null;
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }
    
}
