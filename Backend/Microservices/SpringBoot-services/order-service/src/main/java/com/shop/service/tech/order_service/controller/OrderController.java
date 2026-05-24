package com.shop.service.tech.order_service.controller;

import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.http.HttpStatus;

import com.shop.service.tech.order_service.dto.OrderRequest;
import com.shop.service.tech.order_service.dto.OrderResponse;
import com.shop.service.tech.order_service.service.IOrderService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/orders")
public class OrderController 
{
    @Autowired 
    private IOrderService orderService;

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<OrderResponse> getAllOrders()
    {
        try{
            Thread.sleep(1);
        }catch(InterruptedException e)
        {
            Logger.getLogger(OrderController.class.getName())
                    .info("Thread interrupted: " + e.getMessage());
        }
        return orderService.getAllOrders();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public OrderResponse getOrderById(@RequestParam long id)
    {
        return orderService.getOrderById(id);
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest)
    {
        return orderService.createOrder(orderRequest);
    }

    @PostMapping("/batchOrders")
    @ResponseStatus(HttpStatus.CREATED)
    public List<OrderResponse> createOrders(@RequestBody List<OrderRequest> orderRequests)
    {
        return orderService.createOrder(orderRequests);
    }
    
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderResponse updateOrder(@PathVariable long id, @RequestBody OrderRequest orderRequest) 
    {
        return orderService.updateOrder(id, orderRequest);
    }

    @PutMapping()
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderResponse updateOrder(OrderRequest orderRequest) 
    {
        return orderService.updateOrder(orderRequest);  
    }   

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable long id)
    {
        orderService.deleteOrder(id);
    }
}
