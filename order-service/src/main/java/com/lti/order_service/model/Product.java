package com.lti.order_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private Integer id; // Integer ID as in the Product microservice
    private String productName;
    private Integer price;
    private Integer quantity;
}
