package com.lti.order_service.controller;

import lombok.Data;

@Data
public class CartRequest {
    private Integer productId; // Integer ID for adding to cart
    private Integer quantity;
}

