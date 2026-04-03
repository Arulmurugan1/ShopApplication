package com.shop.service.tech.order_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.service.tech.order_service.dao.Product;
import com.shop.service.tech.order_service.dto.ProductDTO;
import com.shop.service.tech.order_service.repository.IProductRepository;

@Service
public class ProductService implements IProductService 
{

    @Autowired
    private IProductRepository productRepository;

    public List<ProductDTO> getAllProducts()
    {
        List<ProductDTO> products = productRepository.findAll()
            .stream()
            .map(product -> new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getCompany(),
                product.getPrice(),
                product.getPriceType()
            ))
            .toList();
        
        return products;

    }
    
    public ProductDTO getProductById(String id)
    {
        return productRepository.findById(id)
            .map(product -> new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getCompany(),
                product.getPrice(),
                product.getPriceType()
            ))
            .orElse(null);
    }

    public ProductDTO createProduct(ProductDTO productDTO)
    {
        Product product = productRepository.save(
            new Product(
                productDTO.id(),
                productDTO.name(),
                productDTO.description(),
                productDTO.category(),
                productDTO.company(),
                productDTO.price(),
                productDTO.priceType(),
                LocalDateTime.now(),
                LocalDateTime.now()
            ));

        return new ProductDTO(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getCategory(),
            product.getCompany(),
            product.getPrice(),
            product.getPriceType()
        );
    }

    public ProductDTO updateProduct(ProductDTO productDTO)
    {
        return productRepository.findById(productDTO.id())
            .map(product -> {
                product.setName(productDTO.name());
                product.setDescription(productDTO.description());
                product.setCategory(productDTO.category());
                product.setCompany(productDTO.company());
                product.setPrice(productDTO.price());
                product.setPriceType(productDTO.priceType());
                product.setUpdatedAt(LocalDateTime.now());

                Product updatedProduct = productRepository.save(product);

                return new ProductDTO(
                    updatedProduct.getId(),
                    updatedProduct.getName(),
                    updatedProduct.getDescription(),
                    updatedProduct.getCategory(),
                    updatedProduct.getCompany(),
                    updatedProduct.getPrice(),
                    updatedProduct.getPriceType()
                );
            })
            .orElse(null);
    }

    public boolean deleteProduct(String id)
    {
        if(id.equals("all"))
        {
            productRepository.deleteAll();
            return true;
        }
        else if (productRepository.existsById(id)) 
        {
            productRepository.deleteById(id);
            return true;
        }

        return false;
    }
}