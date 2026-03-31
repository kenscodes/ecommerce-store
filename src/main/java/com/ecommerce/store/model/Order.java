package com.ecommerce.store.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Represents a customer order
 */
public class Order {
    private String id;
    private String customerId;
    private List<CartItem> items;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal total;
    private String discountCode;
    private LocalDateTime orderDate;
    private OrderStatus status;

    public Order() {
        this.id = UUID.randomUUID().toString();
        this.orderDate = LocalDateTime.now();
        this.status = OrderStatus.PENDING;
        this.discountAmount = BigDecimal.ZERO;
    }

    public Order(String customerId, List<CartItem> items) {
        this();
        this.customerId = customerId;
        this.items = items;
        this.subtotal = items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.total = subtotal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void applyDiscount(BigDecimal discountPercentage) {
        this.discountAmount = subtotal.multiply(discountPercentage).divide(BigDecimal.valueOf(100));
        this.total = subtotal.subtract(discountAmount);
    }

    public int getTotalItemsCount() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }

    public enum OrderStatus {
        PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
    }
}
