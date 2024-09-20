package com.lti.order_service.controller;


import com.lti.order_service.model.Cart;
import com.lti.order_service.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class CartControllerTest {

    @InjectMocks
    private CartController cartController;

    @Mock
    private CartService cartService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(cartController).build();
    }

    @Test
    void testAddProductToCart_Success() throws Exception {
        Cart mockCart = new Cart();
        mockCart.setProductName("Test Product");

        when(cartService.addProductToCart(anyInt(), anyInt())).thenReturn(mockCart);

        mockMvc.perform(post("/api/cart/add")
                        .contentType("application/json")
                        .content("{\"productId\": 1, \"quantity\": 3}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Product Test Product added to the cart"));
    }

    @Test
    void testAddProductToCart_InvalidData() throws Exception {
        when(cartService.addProductToCart(anyInt(), anyInt())).thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post("/api/cart/add")
                        .contentType("application/json")
                        .content("{\"productId\": 1, \"quantity\": 3}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid data"));
    }

    @Test
    void testRemoveProductFromCart_Success() throws Exception {
        doNothing().when(cartService).removeProductFromCart(anyInt());

        mockMvc.perform(delete("/api/cart/remove/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testRemoveProductFromCart_NotFound() throws Exception {
        doThrow(new RuntimeException("Product not found")).when(cartService).removeProductFromCart(anyInt());

        mockMvc.perform(delete("/api/cart/remove/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllProducts() throws Exception {
        Cart mockCart1 = new Cart();
        mockCart1.setProductName("Product1");

        Cart mockCart2 = new Cart();
        mockCart2.setProductName("Product2");

        when(cartService.getAllProducts()).thenReturn( Arrays.asList(mockCart1, mockCart2));

        mockMvc.perform(get("/api/cart/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Product1"))
                .andExpect(jsonPath("$[1].productName").value("Product2"));
    }
}
