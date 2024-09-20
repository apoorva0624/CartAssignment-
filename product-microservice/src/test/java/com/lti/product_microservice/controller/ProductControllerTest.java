package com.lti.product_microservice.controller;


import com.lti.product_microservice.model.Product;
import com.lti.product_microservice.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    void addProduct_ShouldReturnCreated_WhenProductIsValid() throws Exception {
        Product product = new Product(1, "MacBook Pro", 10699, 2);

        when(productService.addProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"productName\":\"MacBook Pro\",\"price\":10699,\"quantity\":2}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Product added successfully"));
    }

    @Test
    void addProduct_ShouldReturnBadRequest_WhenProductAlreadyExists() throws Exception {
        Product product = new Product(1, "MacBook Pro", 10699, 2);

        doThrow(new IllegalArgumentException("Product with this name already exists."))
                .when(productService).addProduct(any(Product.class));

        mockMvc.perform(MockMvcRequestBuilders.post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":1,\"productName\":\"MacBook Pro\",\"price\":10699,\"quantity\":2}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Product with this name already exists."));
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() throws Exception {
        Product product = new Product(1, "MacBook Pro", 10699, 2);

        when(productService.getProductById(anyInt())).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/1"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.productName").value("MacBook Pro"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.price").value(10699))
                .andExpect(MockMvcResultMatchers.jsonPath("$.quantity").value(2));
    }

    @Test
    void getProductById_ShouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        when(productService.getProductById(anyInt())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.get("/products/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void getAllProducts_ShouldReturnListOfProducts() throws Exception {
        Product product1 = new Product(1, "MacBook Pro", 10699, 2);
        Product product2 = new Product(2, "iPhone", 999, 5);
        List<Product> products = Arrays.asList(product1, product2);

        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(MockMvcRequestBuilders.get("/products"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].productName").value("MacBook Pro"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].productName").value("iPhone"));
    }

    @Test
    void deleteProduct_ShouldReturnNoContent_WhenProductIsDeleted() throws Exception {
        Product product = new Product(1, "MacBook Pro", 10699, 2);

        when(productService.getProductById(anyInt())).thenReturn(product);

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/1"))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().string("Product deleted successfully"));

        verify(productService, times(1)).deleteProduct(anyInt());
    }

    @Test
    void deleteProduct_ShouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        when(productService.getProductById(anyInt())).thenReturn(null);

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/1"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Product not found"));

        verify(productService, times(0)).deleteProduct(anyInt());
    }
}
