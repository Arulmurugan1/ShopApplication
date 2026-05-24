package com.shop.service.tech.product_service.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shop.service.tech.product_service.dto.ProductDTO;
import com.shop.service.tech.product_service.service.IProductService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/products")
public class ProductController 
{
    @Autowired
    private IProductService productService;

    @GetMapping()
    List<ProductDTO> getAllProducts()
    {
        return productService.getAllProducts();
    }   
    
    @GetMapping("/{id}")
    ProductDTO getProductById(@PathVariable String id)
    {
        return productService.getProductById(id);
    }

    @PostMapping()
    ProductDTO createProduct(@RequestBody ProductDTO productDTO)
    {
        return productService.createProduct(productDTO);
    }

    @PutMapping
    ProductDTO updateProduct(@RequestBody ProductDTO productDTO)
    {
        return productService.updateProduct(productDTO);
    }

    @DeleteMapping("/{id}")
    boolean deleteProduct(@PathVariable String id)
    {
        return productService.deleteProduct(id);
    }

    @DeleteMapping("/name/{name}")
    boolean deleteProductByName(@PathVariable String name)
    {
        return productService.deleteProductByName(name);
    }
}
