package com.ecommerce.store.service;

import com.ecommerce.store.dto.AddToCartRequest;
import com.ecommerce.store.model.Cart;
import com.ecommerce.store.model.CartItem;
import com.ecommerce.store.model.Product;
import com.ecommerce.store.repository.InMemoryStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CartServiceTest {

    private CartService cartService;
    private InMemoryStore store;
    private Product testProduct;
    private String testCustomerId = "customer123";

    @BeforeEach
    void setUp() {
        store = new InMemoryStore();
        cartService = new CartService();
        
        // Use reflection to inject the store dependency
        try {
            java.lang.reflect.Field storeField = CartService.class.getDeclaredField("store");
            storeField.setAccessible(true);
            storeField.set(cartService, store);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject store dependency", e);
        }
        
        testProduct = new Product("P001", "Test Product", "Description", new BigDecimal("100.00"), 10);
        store.addProduct(testProduct);
    }

    @Test
    void testGetCartByCustomerId_NewCart() {
        // When
        var response = cartService.getCartByCustomerId(testCustomerId);

        // Then
        assertNotNull(response);
        assertEquals(testCustomerId, response.getCustomerId());
        assertTrue(response.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, response.getTotal());
    }

    @Test
    void testGetCartByCustomerId_ExistingCart() {
        // Given
        Cart existingCart = new Cart(testCustomerId);
        store.saveCart(existingCart);

        // When
        var response = cartService.getCartByCustomerId(testCustomerId);

        // Then
        assertNotNull(response);
        assertEquals(testCustomerId, response.getCustomerId());
        assertEquals(existingCart.getId(), response.getId());
    }

    @Test
    void testAddToCart_Success() {
        // Given
        AddToCartRequest request = new AddToCartRequest("P001", 2);

        // When
        var response = cartService.addToCart(testCustomerId, request);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals("P001", response.getItems().get(0).getProduct().getId());
        assertEquals(2, response.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("200.00"), response.getTotal());
    }

    @Test
    void testAddToCart_ProductNotFound() {
        // Given
        AddToCartRequest request = new AddToCartRequest("P999", 1);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartService.addToCart(testCustomerId, request)
        );
        assertEquals("Product not found: P999", exception.getMessage());
    }

    @Test
    void testAddToCart_InsufficientStock() {
        // Given
        AddToCartRequest request = new AddToCartRequest("P001", 15);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartService.addToCart(testCustomerId, request)
        );
        assertEquals("Insufficient stock for product: P001", exception.getMessage());
    }

    @Test
    void testAddToCart_ExistingItem_UpdatesQuantity() {
        // Given
        AddToCartRequest request1 = new AddToCartRequest("P001", 2);
        AddToCartRequest request2 = new AddToCartRequest("P001", 3);

        // When
        cartService.addToCart(testCustomerId, request1);
        var response = cartService.addToCart(testCustomerId, request2);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(5, response.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("500.00"), response.getTotal());
    }

    @Test
    void testUpdateCartItem_IncreaseQuantity() {
        // Given
        AddToCartRequest addRequest = new AddToCartRequest("P001", 1);
        cartService.addToCart(testCustomerId, addRequest);

        // When
        var response = cartService.updateCartItem(testCustomerId, "P001", 3);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getItems().size());
        assertEquals(3, response.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("300.00"), response.getTotal());
    }

    @Test
    void testUpdateCartItem_RemoveItem() {
        // Given
        AddToCartRequest addRequest = new AddToCartRequest("P001", 2);
        cartService.addToCart(testCustomerId, addRequest);

        // When
        var response = cartService.updateCartItem(testCustomerId, "P001", 0);

        // Then
        assertNotNull(response);
        assertTrue(response.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, response.getTotal());
    }

    @Test
    void testUpdateCartItem_CartNotFound() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cartService.updateCartItem("nonexistent", "P001", 1)
        );
        assertEquals("Cart not found for customer: nonexistent", exception.getMessage());
    }

    @Test
    void testRemoveFromCart_Success() {
        // Given
        AddToCartRequest addRequest = new AddToCartRequest("P001", 2);
        cartService.addToCart(testCustomerId, addRequest);

        // When
        var response = cartService.removeFromCart(testCustomerId, "P001");

        // Then
        assertNotNull(response);
        assertTrue(response.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, response.getTotal());
    }

    @Test
    void testClearCart_Success() {
        // Given
        AddToCartRequest addRequest = new AddToCartRequest("P001", 1);
        cartService.addToCart(testCustomerId, addRequest);

        // When
        cartService.clearCart(testCustomerId);

        // Then
        Cart clearedCart = store.getCartByCustomerId(testCustomerId);
        assertNotNull(clearedCart);
        assertTrue(clearedCart.getItems().isEmpty());
    }

    @Test
    void testCartTotalCalculation() {
        // Given
        Product product2 = new Product("P002", "Product 2", "Description", new BigDecimal("50.00"), 5);
        store.addProduct(product2);
        
        cartService.addToCart(testCustomerId, new AddToCartRequest("P001", 2));
        cartService.addToCart(testCustomerId, new AddToCartRequest("P002", 3));

        // When
        var response = cartService.getCartByCustomerId(testCustomerId);

        // Then
        assertEquals(new BigDecimal("350.00"), response.getTotal()); // 2*100 + 3*50
    }
}
