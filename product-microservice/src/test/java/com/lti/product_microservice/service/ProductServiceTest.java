package com.lti.product_microservice.service;


import com.lti.product_microservice.model.Product;
import com.lti.product_microservice.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addProduct_ShouldThrowException_WhenProductAlreadyExists() {
        Product product = new Product(1, "MacBook Pro", 10699, 2);

        when(productRepository.findByProductName(any(String.class))).thenReturn(product);

        assertThrows(IllegalArgumentException.class, () -> productService.addProduct(product));
    }

    @Test
    void addProduct_ShouldSaveProduct_WhenProductIsUnique() {
        Product product = new Product(1, "MacBook Pro", 10699, 2);

        when(productRepository.findByProductName(any(String.class))).thenReturn(null);
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.addProduct(product);

        assertEquals(product, result);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() {
        Product product = new Product(1, "MacBook Pro", 10699, 2);

        when(productRepository.findById(anyInt())).thenReturn(Optional.of(product));

        Product result = productService.getProductById(1);

        assertEquals(product, result);
    }

    @Test
    void getProductById_ShouldReturnNull_WhenProductDoesNotExist() {
        when(productRepository.findById(anyInt())).thenReturn(Optional.empty());

        Product result = productService.getProductById(1);

        assertEquals(null, result);
    }

    @Test
    void deleteProduct_ShouldInvokeDeleteById() {
        doNothing().when(productRepository).deleteById(anyInt());

        productService.deleteProduct(1);

        verify(productRepository, times(1)).deleteById(1);
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() {
        Product product1 = new Product(1, "MacBook Pro", 10699, 2);
        Product product2 = new Product(2, "iPhone", 999, 5);
        List<Product> products = Arrays.asList(product1, product2);

        when(productRepository.findAll()).thenReturn(products);

        List<Product> result = productService.getAllProducts();

        assertEquals(products, result);
    }
}
