package com.shop.service.tech.order_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.shop.service.tech.order_service.dao.Product;

public interface IProductRepository extends MongoRepository<Product, String> {
    
}
