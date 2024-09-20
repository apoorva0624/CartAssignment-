package com.lti.order_service.service;

import com.lti.order_service.model.Cart;
import com.lti.order_service.model.Product;
import com.lti.order_service.repository.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddProductToCart_Success_NewProduct() {
        Integer productId = 1;
        Integer quantity = 2;

        Product product = new Product(productId, "Test Product", 100, 10);
        when(productServiceClient.getProductById(productId)).thenReturn(product);
        when(cartRepository.findByProductId(productId)).thenReturn(Optional.empty());

        Cart newCart = new Cart(productId, "Test Product", 100, quantity);
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

        Cart result = cartService.addProductToCart(productId, quantity);

        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(quantity, result.getQuantity());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void testAddProductToCart_Success_ExistingProduct() {
        Integer productId = 1;
        Integer quantity = 2;

        Product product = new Product(productId, "Test Product", 100, 10);
        Cart existingCart = new Cart(productId, "Test Product", 100, 1);

        when(productServiceClient.getProductById(productId)).thenReturn(product);
        when(cartRepository.findByProductId(productId)).thenReturn(Optional.of(existingCart));

        Cart updatedCart = new Cart(productId, "Test Product", 100, existingCart.getQuantity() + quantity);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart result = cartService.addProductToCart(productId, quantity);

        assertNotNull(result);
        assertEquals(productId, result.getProductId());
        assertEquals(existingCart.getQuantity() + quantity, result.getQuantity());
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void testAddProductToCart_ProductNotFound() {
        Integer productId = 1;
        Integer quantity = 2;

        when(productServiceClient.getProductById(productId)).thenReturn(null);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            cartService.addProductToCart(productId, quantity);
        });
        assertEquals("Product not found", thrown.getMessage());
    }

    @Test
    void testAddProductToCart_QuantityInvalid() {
        Integer productId = 1;
        Integer quantity = -1;

        Product product = new Product(productId, "Test Product", 100, 10);
        when(productServiceClient.getProductById(productId)).thenReturn(product);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            cartService.addProductToCart(productId, quantity);
        });
        assertEquals("Quantity must be greater than zero", thrown.getMessage());
    }

    @Test
    void testAddProductToCart_QuantityExceedsStock() {
        Integer productId = 1;
        Integer quantity = 15;

        Product product = new Product(productId, "Test Product", 100, 10);
        when(productServiceClient.getProductById(productId)).thenReturn(product);

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            cartService.addProductToCart(productId, quantity);
        });
        assertEquals("Requested quantity exceeds available stock", thrown.getMessage());
    }

    @Test
    void testRemoveProductFromCart_Success() {
        Integer productId = 1;
        Cart cart = new Cart(productId, "Test Product", 100, 2);

        when(cartRepository.findByProductId(productId)).thenReturn(Optional.of(cart));

        cartService.removeProductFromCart(productId);

        verify(cartRepository).delete(cart);
    }

    @Test
    void testRemoveProductFromCart_NotFound() {
        Integer productId = 1;

        when(cartRepository.findByProductId(productId)).thenReturn(Optional.empty());

        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            cartService.removeProductFromCart(productId);
        });
        assertEquals("Product with ID " + productId + " not found in the cart", thrown.getMessage());
    }

    // @Test
    // void testGetAllProducts() {
    //     Iterable<Cart> carts = mock(Iterable.class);
    //
    //     when(cartRepository.findAll()).thenReturn((List<Cart>) carts);
    //
    //     Iterable<Cart> result = cartService.getAllProducts();
    //
    //     assertNotNull(result);
    //     verify(cartRepository).findAll();
    // }
}
