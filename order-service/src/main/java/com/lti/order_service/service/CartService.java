package com.lti.order_service.service;

import com.lti.order_service.model.Cart;
import com.lti.order_service.model.Product;
import com.lti.order_service.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductServiceClient productServiceClient;

    public Cart addProductToCart(Integer productId, Integer quantity) {
        // Fetch product details from the product microservice
        Product product = productServiceClient.getProductById(productId);
        if (product == null) {
            throw new RuntimeException("Product not found");
        }

        // Validate quantity
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (quantity > product.getQuantity()) {
            throw new RuntimeException("Requested quantity exceeds available stock");
        }

        // Check if the product already exists in the cart
        Optional<Cart> existingCart = cartRepository.findByProductId(productId);
        if (existingCart.isPresent()) {
            // Increase the quantity of the existing product
            Cart existing = existingCart.get();
            int newQuantity = existing.getQuantity() + quantity;
            if (newQuantity > product.getQuantity()) {
                throw new RuntimeException("Requested quantity exceeds available stock");
            }
            existing.setQuantity(newQuantity);
            return cartRepository.save(existing);
        }

        // Set the product details and save new cart entry
        Cart newCart = new Cart(productId, product.getProductName(), product.getPrice(), quantity);
        return cartRepository.save(newCart);
    }

    public void removeProductFromCart(Integer productId) {
        Optional<Cart> cart = cartRepository.findByProductId(productId);
        if (cart.isPresent()) {
            cartRepository.delete(cart.get());
        } else {
            throw new RuntimeException("Product with ID " + productId + " not found in the cart");
        }
    }

    public Iterable<Cart> getAllProducts() {
        return cartRepository.findAll();
    }
}
