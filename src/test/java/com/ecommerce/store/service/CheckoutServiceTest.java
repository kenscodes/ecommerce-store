package com.ecommerce.store.service;

import com.ecommerce.store.dto.CheckoutRequest;
import com.ecommerce.store.model.*;
import com.ecommerce.store.repository.InMemoryStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CheckoutServiceTest {

    private CheckoutService checkoutService;
    private CartService cartService;
    private InMemoryStore store;
    private Product testProduct;
    private Cart testCart;
    private String testCustomerId = "customer123";

    @BeforeEach
    void setUp() {
        store = new InMemoryStore();
        cartService = new CartService();
        checkoutService = new CheckoutService();
        
        // Use reflection to inject dependencies
        try {
            java.lang.reflect.Field storeField = CheckoutService.class.getDeclaredField("store");
            storeField.setAccessible(true);
            storeField.set(checkoutService, store);
            
            java.lang.reflect.Field cartServiceField = CheckoutService.class.getDeclaredField("cartService");
            cartServiceField.setAccessible(true);
            cartServiceField.set(checkoutService, cartService);
            
            java.lang.reflect.Field cartServiceStoreField = CartService.class.getDeclaredField("store");
            cartServiceStoreField.setAccessible(true);
            cartServiceStoreField.set(cartService, store);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject dependencies", e);
        }
        
        testProduct = new Product("P001", "Test Product", "Description", new BigDecimal("100.00"), 10);
        store.addProduct(testProduct);
        testCart = new Cart(testCustomerId);
        CartItem cartItem = new CartItem(testProduct, 2);
        testCart.addItem(cartItem);
        store.saveCart(testCart);
    }

    @Test
    void testCheckout_SuccessWithoutDiscount() {
        // Given
        CheckoutRequest request = new CheckoutRequest(testCustomerId, null);

        // When
        Order order = checkoutService.checkout(request);

        // Then
        assertNotNull(order);
        assertEquals(testCustomerId, order.getCustomerId());
        assertEquals(new BigDecimal("200.00"), order.getSubtotal());
        assertEquals(new BigDecimal("200.00"), order.getTotal());
        assertEquals(BigDecimal.ZERO, order.getDiscountAmount());
        assertNull(order.getDiscountCode());
        assertEquals(Order.OrderStatus.CONFIRMED, order.getStatus());
        
        // Verify cart is cleared
        Cart clearedCart = store.getCartByCustomerId(testCustomerId);
        assertTrue(clearedCart == null || clearedCart.getItems().isEmpty());
    }

    @Test
    void testCheckout_SuccessWithValidDiscount() {
        // Given
        Discount discount = new Discount("SAVE10", BigDecimal.valueOf(10));
        store.saveDiscount(discount);
        CheckoutRequest request = new CheckoutRequest(testCustomerId, "SAVE10");

        // When
        Order order = checkoutService.checkout(request);

        // Then
        assertNotNull(order);
        assertEquals(testCustomerId, order.getCustomerId());
        assertEquals(new BigDecimal("200.00"), order.getSubtotal());
        assertEquals(new BigDecimal("180.00"), order.getTotal());
        assertEquals(new BigDecimal("20.00"), order.getDiscountAmount());
        assertEquals("SAVE10", order.getDiscountCode());
        assertEquals(Order.OrderStatus.CONFIRMED, order.getStatus());
        
        // Verify discount is marked as used
        Discount usedDiscount = store.getDiscount("SAVE10");
        assertTrue(usedDiscount.isUsed());
    }

    @Test
    void testCheckout_EmptyCart() {
        // Given
        CheckoutRequest request = new CheckoutRequest("emptyCustomer", null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> checkoutService.checkout(request)
        );
        assertEquals("Cart is empty or not found", exception.getMessage());
    }

    @Test
    void testCheckout_InvalidDiscountCode() {
        // Given
        CheckoutRequest request = new CheckoutRequest(testCustomerId, "INVALID");

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> checkoutService.checkout(request)
        );
        assertEquals("Invalid discount code: INVALID", exception.getMessage());
    }

    @Test
    void testCheckout_UsedDiscountCode() {
        // Given
        Discount usedDiscount = new Discount("USED10", BigDecimal.valueOf(10));
        usedDiscount.setUsed(true);
        store.saveDiscount(usedDiscount);
        CheckoutRequest request = new CheckoutRequest(testCustomerId, "USED10");

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> checkoutService.checkout(request)
        );
        assertEquals("Discount code already used: USED10", exception.getMessage());
    }

    @Test
    void testCheckout_InsufficientStock() {
        // Given
        Product lowStockProduct = new Product("P001", "Test Product", "Description", new BigDecimal("100.00"), 1);
        store.addProduct(lowStockProduct);
        CheckoutRequest request = new CheckoutRequest(testCustomerId, null);

        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> checkoutService.checkout(request)
        );
        assertEquals("Insufficient stock for product: P001", exception.getMessage());
    }

    @Test
    void testGetOrder_Success() {
        // Given
        CheckoutRequest request = new CheckoutRequest(testCustomerId, null);
        Order placedOrder = checkoutService.checkout(request);

        // When
        Order result = checkoutService.getOrder(placedOrder.getId());

        // Then
        assertNotNull(result);
        assertEquals(placedOrder.getId(), result.getId());
        assertEquals(testCustomerId, result.getCustomerId());
    }

    @Test
    void testGetOrder_NotFound() {
        // When & Then
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> checkoutService.getOrder("NONEXISTENT")
        );
        assertEquals("Order not found: NONEXISTENT", exception.getMessage());
    }

    @Test
    void testCheckout_UpdatesAnalytics() {
        // Given
        long initialOrderCount = store.getTotalOrders();
        long initialItemsSold = store.getTotalItemsSold();
        CheckoutRequest request = new CheckoutRequest(testCustomerId, null);

        // When
        checkoutService.checkout(request);

        // Then
        assertEquals(initialOrderCount + 1, store.getTotalOrders());
        assertEquals(initialItemsSold + 2, store.getTotalItemsSold());
        assertEquals(new BigDecimal("200"), store.getTotalRevenue());
    }

    @Test
    void testCheckout_WithDiscount_UpdatesDiscountAnalytics() {
        // Given
        Discount discount = new Discount("SAVE20", BigDecimal.valueOf(20));
        store.saveDiscount(discount);
        CheckoutRequest request = new CheckoutRequest(testCustomerId, "SAVE20");

        // When
        checkoutService.checkout(request);

        // Then
        assertEquals(1, store.getUsedDiscountCodes());
        assertEquals(new BigDecimal("40"), store.getTotalDiscountGiven()); // 20% of 200
    }
}
