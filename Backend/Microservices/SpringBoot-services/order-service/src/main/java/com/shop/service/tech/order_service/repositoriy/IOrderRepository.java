package com.shop.service.tech.order_service.repositoriy;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shop.service.tech.order_service.dao.Order;

public interface IOrderRepository extends JpaRepository<Order,Long>
{
       
}
