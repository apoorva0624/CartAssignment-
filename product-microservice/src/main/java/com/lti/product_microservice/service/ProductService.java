package com.lti.product_microservice.service;


import com.lti.product_microservice.model.Product;
import com.lti.product_microservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Product addProduct(Product product) {
        // Check if product already exists
        if(productRepository.findByProductName(product.getProductName()) != null) {
            throw new IllegalArgumentException("Product with this name already exists.");
        }
        return productRepository.save(product);
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}

