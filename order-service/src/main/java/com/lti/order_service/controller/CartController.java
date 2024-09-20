package com.lti.order_service.controller;


import com.lti.order_service.model.Cart;
import com.lti.order_service.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Operation(summary = "Add a product to the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product added to the cart"),
            @ApiResponse(responseCode = "400", description = "Invalid product data or quantity exceeds stock"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PostMapping("/add")
    public ResponseEntity<String> addProductToCart(@RequestBody CartRequest cartRequest) {
        try {
            Cart cart = cartService.addProductToCart(cartRequest.getProductId(), cartRequest.getQuantity());
            return new ResponseEntity<>(String.format("Product %s added to the cart", cart.getProductName()), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Remove a product from the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product removed from the cart"),
            @ApiResponse(responseCode = "404", description = "Product not found in the cart")
    })
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<Void> removeProductFromCart(@PathVariable Integer productId) {
        try {
            cartService.removeProductFromCart(productId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get all products in the cart")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of products in the cart")
    })
    @GetMapping("/all")
    public Iterable<Cart> getAllProducts() {
        return cartService.getAllProducts();
    }
}
