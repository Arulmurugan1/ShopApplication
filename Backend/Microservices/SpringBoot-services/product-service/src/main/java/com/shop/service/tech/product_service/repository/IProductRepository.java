package com.shop.service.tech.product_service.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.shop.service.tech.product_service.dao.Product;

public interface IProductRepository extends MongoRepository<Product, String> {
    boolean existsByName(String name);
    void deleteByName(String name);
}

