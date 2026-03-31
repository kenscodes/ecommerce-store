package com.ecommerce.store.repository;

import com.ecommerce.store.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryStoreTest {

    private InMemoryStore store;

    @BeforeEach
    void setUp() {
        store = new InMemoryStore();
    }

    @Test
    void testProductOperations() {
        // Test getting all products
        List<Product> products = store.getAllProducts();
        assertFalse(products.isEmpty());
        assertEquals(5, products.size()); // 5 sample products should be initialized

        // Test getting specific product
        Product laptop = store.getProduct("P001");
        assertNotNull(laptop);
        assertEquals("Laptop", laptop.getName());
        assertEquals(new BigDecimal("999.99"), laptop.getPrice());

        // Test adding new product
        Product newProduct = new Product("P006", "Tablet", "10-inch tablet", new BigDecimal("299.99"), 20);
        store.addProduct(newProduct);
        
        Product retrieved = store.getProduct("P006");
        assertNotNull(retrieved);
        assertEquals("Tablet", retrieved.getName());
    }

    @Test
    void testCartOperations() {
        // Test creating and saving cart
        Cart cart = new Cart("customer123");
        store.saveCart(cart);

        // Test retrieving cart
        Cart retrieved = store.getCart(cart.getId());
        assertNotNull(retrieved);
        assertEquals("customer123", retrieved.getCustomerId());

        // Test getting cart by customer ID
        Cart customerCart = store.getCartByCustomerId("customer123");
        assertNotNull(customerCart);
        assertEquals(cart.getId(), customerCart.getId());

        // Test deleting cart
        store.deleteCart(cart.getId());
        assertNull(store.getCart(cart.getId()));
    }

    @Test
    void testOrderOperations() {
        // Create and save order
        Product product = store.getProduct("P001");
        CartItem item = new CartItem(product, 2);
        Order order = new Order("customer123", List.of(item));
        
        long initialOrderCount = store.getTotalOrders();
        store.saveOrder(order);

        // Test retrieving order
        Order retrieved = store.getOrder(order.getId());
        assertNotNull(retrieved);
        assertEquals("customer123", retrieved.getCustomerId());
        assertEquals(new BigDecimal("1999.98"), retrieved.getTotal());

        // Test analytics updated
        assertEquals(initialOrderCount + 1, store.getTotalOrders());
        assertEquals(2, store.getTotalItemsSold());
        assertEquals(new BigDecimal("1999.98"), store.getTotalRevenue());
    }

    @Test
    void testDiscountOperations() {
        // Create and save discount
        Discount discount = new Discount("SAVE20", BigDecimal.valueOf(20));
        store.saveDiscount(discount);

        // Test retrieving discount
        Discount retrieved = store.getDiscount("SAVE20");
        assertNotNull(retrieved);
        assertEquals("SAVE20", retrieved.getCode());
        assertEquals(BigDecimal.valueOf(20), retrieved.getPercentage());

        // Test case insensitive lookup
        Discount caseInsensitive = store.getDiscount("save20");
        assertNotNull(caseInsensitive);
        assertEquals("SAVE20", caseInsensitive.getCode());

        // Test getting all discounts
        List<Discount> allDiscounts = store.getAllDiscounts();
        assertFalse(allDiscounts.isEmpty());
    }

    @Test
    void testDiscountGeneration() {
        // Initially no discounts should be generated for first order
        Discount discount1 = store.generateDiscountForOrder();
        assertNull(discount1);

        // Create orders to reach the threshold (every 3rd order)
        for (int i = 0; i < 2; i++) {
            Product product = store.getProduct("P002");
            CartItem item = new CartItem(product, 1);
            Order order = new Order("customer" + i, List.of(item));
            store.saveOrder(order);
        }

        // Third order should generate a discount
        Discount discount3 = store.generateDiscountForOrder();
        assertNotNull(discount3);
        assertEquals(BigDecimal.valueOf(10), discount3.getPercentage()); // Default 10%
        assertFalse(discount3.isUsed());

        // Create the order that uses the discount
        Product product = store.getProduct("P003");
        CartItem item = new CartItem(product, 1);
        Order order = new Order("customer3", List.of(item));
        store.saveOrder(order);

        // Next order shouldn't generate discount until 6th order
        Discount discount4 = store.generateDiscountForOrder();
        assertNull(discount4);
    }

    @Test
    void testDiscountUsage() {
        // Create and use a discount
        Discount discount = new Discount("SAVE10", BigDecimal.valueOf(10));
        store.saveDiscount(discount);

        assertEquals(0, store.getUsedDiscountCodes());

        discount.setUsed(true);
        store.saveDiscount(discount);

        assertEquals(1, store.getUsedDiscountCodes());
        assertTrue(discount.isUsed());
        assertNotNull(discount.getUsedAt());
    }

    @Test
    void testAnalytics() {
        // Initial state
        assertEquals(0, store.getTotalOrders());
        assertEquals(0, store.getTotalItemsSold());
        assertEquals(BigDecimal.ZERO, store.getTotalRevenue());
        assertEquals(BigDecimal.ZERO, store.getTotalDiscountGiven());

        // Create some orders
        Product product1 = store.getProduct("P001");
        Product product2 = store.getProduct("P002");
        
        CartItem item1 = new CartItem(product1, 1);
        Order order1 = new Order("customer1", List.of(item1));
        store.saveOrder(order1);

        CartItem item2 = new CartItem(product2, 2);
        Order order2 = new Order("customer2", List.of(item2));
        store.saveOrder(order2);

        // Check analytics
        assertEquals(2, store.getTotalOrders());
        assertEquals(3, store.getTotalItemsSold());
        assertEquals(new BigDecimal("1059.97"), store.getTotalRevenue()); // 999.99 + 2*29.99

        // Test with discount
        Discount discount = new Discount("SAVE10", BigDecimal.valueOf(10));
        store.saveDiscount(discount);
        
        Order orderWithDiscount = new Order("customer3", List.of(item1));
        orderWithDiscount.applyDiscount(BigDecimal.valueOf(10));
        orderWithDiscount.setDiscountCode("SAVE10");
        store.saveOrder(orderWithDiscount);

        assertEquals(new BigDecimal("1959.96"), store.getTotalRevenue()); // Previous + (999.99 - 99.999)
        assertEquals(new BigDecimal("99.99"), store.getTotalDiscountGiven()); // 10% of 999.99
    }

    @Test
    void testDiscountSettings() {
        // Test default settings
        assertEquals(3, store.getDiscountEveryNthOrder());
        assertEquals(BigDecimal.valueOf(10), store.getDiscountPercentage());

        // Test updating settings
        store.setDiscountEveryNthOrder(5);
        store.setDiscountPercentage(BigDecimal.valueOf(15));

        assertEquals(5, store.getDiscountEveryNthOrder());
        assertEquals(BigDecimal.valueOf(15), store.getDiscountPercentage());
    }

    @Test
    void testClearAll() {
        // Add some data
        Product product = store.getProduct("P001");
        Cart cart = new Cart("customer123");
        CartItem item = new CartItem(product, 1);
        Order order = new Order("customer123", List.of(item));
        Discount discount = new Discount("TEST", BigDecimal.valueOf(5));

        store.saveCart(cart);
        store.saveOrder(order);
        store.saveDiscount(discount);

        // Verify data exists
        assertNotNull(store.getCart(cart.getId()));
        assertNotNull(store.getOrder(order.getId()));
        assertNotNull(store.getDiscount("TEST"));
        assertTrue(store.getTotalOrders() > 0);

        // Clear all
        store.clearAll();

        // Verify only sample products remain
        assertEquals(5, store.getAllProducts().size());
        assertEquals(0, store.getAllOrders().size());
        assertEquals(0, store.getAllDiscounts().size());
        assertEquals(0, store.getTotalOrders());
        assertEquals(0, store.getTotalItemsSold());
        assertEquals(BigDecimal.ZERO, store.getTotalRevenue());
        assertEquals(BigDecimal.ZERO, store.getTotalDiscountGiven());
    }
}
