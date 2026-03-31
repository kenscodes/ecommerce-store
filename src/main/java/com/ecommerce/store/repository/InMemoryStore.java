package com.ecommerce.store.repository;

import com.ecommerce.store.model.Cart;
import com.ecommerce.store.model.Discount;
import com.ecommerce.store.model.Order;
import com.ecommerce.store.model.Product;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory storage for all entities
 */
@Repository
public class InMemoryStore {
    
    // Product storage
    private final Map<String, Product> products = new ConcurrentHashMap<>();
    
    // Cart storage
    private final Map<String, Cart> carts = new ConcurrentHashMap<>();
    
    // Order storage
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    
    // Discount storage
    private final Map<String, Discount> discounts = new ConcurrentHashMap<>();
    
    // Analytics counters
    private final AtomicLong totalOrders = new AtomicLong(0);
    private final AtomicLong totalItemsSold = new AtomicLong(0);
    private final AtomicLong totalRevenue = new AtomicLong(0); // stored in cents to avoid floating point issues
    private final AtomicLong totalDiscountGiven = new AtomicLong(0); // stored in cents
    
    // Discount generation settings
    private int discountEveryNthOrder = 3; // Every 3rd order gets a discount
    private BigDecimal discountPercentage = BigDecimal.valueOf(10); // 10% discount
    
    public InMemoryStore() {
        initializeSampleData();
    }
    
    private void initializeSampleData() {
        // Add some sample products
        products.put("P001", new Product("P001", "Laptop", "High-performance laptop", new BigDecimal("999.99"), 50));
        products.put("P002", new Product("P002", "Mouse", "Wireless optical mouse", new BigDecimal("29.99"), 100));
        products.put("P003", new Product("P003", "Keyboard", "Mechanical keyboard", new BigDecimal("79.99"), 75));
        products.put("P004", new Product("P004", "Monitor", "24-inch HD monitor", new BigDecimal("199.99"), 30));
        products.put("P005", new Product("P005", "Headphones", "Noise-cancelling headphones", new BigDecimal("149.99"), 40));
    }
    
    // Product operations
    public Product getProduct(String id) {
        return products.get(id);
    }
    
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }
    
    public void addProduct(Product product) {
        products.put(product.getId(), product);
    }
    
    // Cart operations
    public Cart getCart(String cartId) {
        return carts.get(cartId);
    }
    
    public Cart getCartByCustomerId(String customerId) {
        return carts.values().stream()
                .filter(cart -> customerId.equals(cart.getCustomerId()))
                .findFirst()
                .orElse(null);
    }
    
    public void saveCart(Cart cart) {
        carts.put(cart.getId(), cart);
    }
    
    public void deleteCart(String cartId) {
        carts.remove(cartId);
    }
    
    // Order operations
    public Order getOrder(String orderId) {
        return orders.get(orderId);
    }
    
    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }
    
    public List<Order> getOrdersByCustomerId(String customerId) {
        return orders.values().stream()
                .filter(order -> customerId.equals(order.getCustomerId()))
                .toList();
    }
    
    public void saveOrder(Order order) {
        orders.put(order.getId(), order);
        
        // Update analytics
        totalOrders.incrementAndGet();
        totalItemsSold.addAndGet(order.getTotalItemsCount());
        
        // Convert to cents for storage
        long totalCents = order.getTotal().multiply(BigDecimal.valueOf(100)).longValue();
        long discountCents = order.getDiscountAmount().multiply(BigDecimal.valueOf(100)).longValue();
        
        totalRevenue.addAndGet(totalCents);
        totalDiscountGiven.addAndGet(discountCents);
    }
    
    // Discount operations
    public Discount getDiscount(String code) {
        return discounts.get(code.toLowerCase());
    }
    
    public List<Discount> getAllDiscounts() {
        return new ArrayList<>(discounts.values());
    }
    
    public void saveDiscount(Discount discount) {
        discounts.put(discount.getCode().toLowerCase(), discount);
    }
    
    public Discount generateDiscountForOrder() {
        long currentOrderCount = totalOrders.get();
        
        // Check if this order qualifies for a discount
        if ((currentOrderCount + 1) % discountEveryNthOrder == 0) {
            String code = generateDiscountCode();
            Discount discount = new Discount(code, discountPercentage);
            saveDiscount(discount);
            return discount;
        }
        
        return null;
    }
    
    private String generateDiscountCode() {
        return "DISCOUNT" + System.currentTimeMillis() % 100000;
    }
    
    // Analytics operations
    public long getTotalOrders() {
        return totalOrders.get();
    }
    
    public long getTotalItemsSold() {
        return totalItemsSold.get();
    }
    
    public BigDecimal getTotalRevenue() {
        return BigDecimal.valueOf(totalRevenue.get()).divide(BigDecimal.valueOf(100));
    }
    
    public BigDecimal getTotalDiscountGiven() {
        return BigDecimal.valueOf(totalDiscountGiven.get()).divide(BigDecimal.valueOf(100));
    }
    
    public long getTotalDiscountCodes() {
        return discounts.size();
    }
    
    public long getUsedDiscountCodes() {
        return discounts.values().stream()
                .mapToLong(d -> d.isUsed() ? 1 : 0)
                .sum();
    }
    
    // Configuration
    public void setDiscountEveryNthOrder(int n) {
        this.discountEveryNthOrder = n;
    }
    
    public void setDiscountPercentage(BigDecimal percentage) {
        this.discountPercentage = percentage;
    }
    
    public int getDiscountEveryNthOrder() {
        return discountEveryNthOrder;
    }
    
    public BigDecimal getDiscountPercentage() {
        return discountPercentage;
    }
    
    // Clear all data (useful for testing)
    public void clearAll() {
        products.clear();
        carts.clear();
        orders.clear();
        discounts.clear();
        totalOrders.set(0);
        totalItemsSold.set(0);
        totalRevenue.set(0);
        totalDiscountGiven.set(0);
        initializeSampleData();
    }
}
