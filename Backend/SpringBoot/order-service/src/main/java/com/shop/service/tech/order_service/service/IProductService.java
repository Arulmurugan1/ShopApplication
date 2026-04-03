package com.shop.service.tech.order_service.service;

import java.util.List;
import com.shop.service.tech.order_service.dto.ProductDTO;

public interface IProductService
{
    List<ProductDTO> getAllProducts();   
    
    ProductDTO getProductById(String id);

    ProductDTO createProduct(ProductDTO productDTO);

    ProductDTO updateProduct(ProductDTO productDTO);

    boolean deleteProduct(String id);

}
