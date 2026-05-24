package com.shop.service.tech.product_service.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.shop.service.tech.product_service.dao.Product;
import com.shop.service.tech.product_service.dto.ProductDTO;
import com.shop.service.tech.product_service.exception.ResourceNotFoundException;
import com.shop.service.tech.product_service.repository.IProductRepository;

@Service
public class ProductService implements IProductService 
{
    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private IProductRepository productRepository;

    public List<ProductDTO> getAllProducts()
    {
        logger.info("Entering getAllProducts() method");
        try {
            logger.debug("Fetching all products from repository");
            List<ProductDTO> products = productRepository.findAll()
                .stream()
                .map(product -> {
                    logger.debug("Mapping product with id: {}, name: {}", product.getId(), product.getName());
                    return new ProductDTO(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getCategory(),
                        product.getCompany(),
                        product.getPrice(),
                        product.getPriceType()
                    );
                })
                .toList();
            
            logger.info("Successfully retrieved {} products from database", products.size());
            return products;
        } catch (Exception e) {
            logger.error("Error occurred in getAllProducts(): {}", e.getMessage(), e);
            throw e;
        }
    }
    
    public ProductDTO getProductById(String id)
    {
        logger.info("Entering getProductById() method with id: {}", id);
        try {
            logger.debug("Searching for product with id: {}", id);
            ProductDTO result = productRepository.findById(id)
                .map(product -> {
                    logger.debug("Product found with id: {}, name: {}", product.getId(), product.getName());
                    return new ProductDTO(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getCategory(),
                        product.getCompany(),
                        product.getPrice(),
                        product.getPriceType()
                    );
                })
                .orElseThrow(() -> {
                    logger.warn("Product not found with id: {}", id);
                    return new ResourceNotFoundException("Product with id " + id + " not found.");
                });
            logger.info("Successfully retrieved product with id: {}", id);
            return result;
        } catch (Exception e) {
            logger.error("Error occurred in getProductById(id={}): {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public ProductDTO createProduct(ProductDTO productDTO)
    {
        logger.info("Entering createProduct() method");
        logger.debug("Product request received - name: {}, category: {}, company: {}, price: {}", 
            productDTO.name(), productDTO.category(), productDTO.company(), productDTO.price());
        try {
            logger.debug("Creating new Product instance with name: {}", productDTO.name());
            Product product = 
                new Product(
                    null,
                    productDTO.name(),
                    productDTO.description(),
                    productDTO.category(),
                    productDTO.company(),
                    productDTO.price(),
                    productDTO.priceType(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
                );

            logger.debug("Checking if product with name '{}' already exists", productDTO.name());
            if( product.getName() != null && productRepository.existsByName(product.getName()) )
            {
                logger.error("Product with name '{}' already exists in database", product.getName());
                throw new RuntimeException("Product with name '" + product.getName() + "' already exists.");
            }             

            logger.debug("Saving product with name: {} to database", product.getName());
            product = productRepository.save(product);
            logger.info("Product successfully created with id: {}, name: {}", product.getId(), product.getName());

            ProductDTO result = new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getCompany(),
                product.getPrice(),
                product.getPriceType()
            );
            logger.debug("Returning ProductDTO for created product with id: {}", result.id());
            return result;
        } catch (Exception e) {
            logger.error("Error occurred in createProduct(name={}): {}", productDTO.name(), e.getMessage(), e);
            throw e;
        }
    }

    public ProductDTO updateProduct(ProductDTO productDTO)
    {
        logger.info("Entering updateProduct() method with id: {}", productDTO.id());
        logger.debug("Product update request - id: {}, name: {}, category: {}, price: {}", 
            productDTO.id(), productDTO.name(), productDTO.category(), productDTO.price());
        try {
            logger.debug("Searching for product with id: {} to update", productDTO.id());
            ProductDTO result = productRepository.findById(productDTO.id())
                .map(product -> {
                    logger.debug("Product found with id: {}, current name: {}", product.getId(), product.getName());
                    logger.debug("Updating product fields...");
                    product.setName(productDTO.name());
                    product.setDescription(productDTO.description());
                    product.setCategory(productDTO.category());
                    product.setCompany(productDTO.company());
                    product.setPrice(productDTO.price());
                    product.setPriceType(productDTO.priceType());
                    product.setUpdatedAt(LocalDateTime.now());
                    logger.debug("All fields updated, saving to database");

                    Product updatedProduct = productRepository.save(product);
                    logger.info("Product successfully updated with id: {}, new name: {}", updatedProduct.getId(), updatedProduct.getName());

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
                .orElseThrow(() -> {
                    logger.warn("Product not found with id: {} for update", productDTO.id());
                    return new ResourceNotFoundException("Product with id " + productDTO.id() + " not found.");
                });
            return result;
        } catch (Exception e) {
            logger.error("Error occurred in updateProduct(id={}): {}", productDTO.id(), e.getMessage(), e);
            throw e;
        }
    }

    public boolean deleteProduct(String id)
    {
        logger.info("Entering deleteProduct() method with id: {}", id);
        try {
            logger.debug("Checking if delete request is for all products");
            if(id.equals("all"))
            {
                logger.warn("Request to delete ALL products");
                productRepository.deleteAll();
                logger.info("All products successfully deleted from database");
                return true;
            }
            
            logger.debug("Checking if product with id: {} exists", id);
            if (productRepository.existsById(id)) 
            {
                logger.debug("Product found with id: {}, proceeding with deletion", id);
                productRepository.deleteById(id);
                logger.info("Product successfully deleted with id: {}", id);
                return true;
            }

            logger.warn("Product not found with id: {} for deletion", id);
            throw new ResourceNotFoundException("Product with id " + id + " not found.");
        } catch (Exception e) {
            logger.error("Error occurred in deleteProduct(id={}): {}", id, e.getMessage(), e);
            throw e;
        }
    }

    public boolean deleteProductByName(String name)
    {
        logger.info("Entering deleteProductByName() method with name: {}", name);
        try {
            logger.debug("Checking if product with name: {} exists", name);
            if (productRepository.existsByName(name))
            {
                logger.debug("Product found with name: {}, proceeding with deletion", name);
                productRepository.deleteByName(name);
                logger.info("Product successfully deleted with name: {}", name);
                return true;
            }

            logger.warn("Product not found with name: {} for deletion", name);
            throw new ResourceNotFoundException("Product with name " + name + " not found.");
        } catch (Exception e) {
            logger.error("Error occurred in deleteProductByName(name={}): {}", name, e.getMessage(), e);
            throw e;
        }
    }
}