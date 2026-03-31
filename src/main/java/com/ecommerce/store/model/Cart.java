package com.ecommerce.store.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a shopping cart
 */
public class Cart {
    private String id;
    private String customerId;
    private List<CartItem> items;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Cart() {
        this.id = UUID.randomUUID().toString();
        this.items = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Cart(String customerId) {
        this();
        this.customerId = customerId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public BigDecimal getTotal() {
        return items.stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void addItem(CartItem item) {
        // Check if item already exists and update quantity
        for (CartItem existingItem : items) {
            if (existingItem.getProduct().getId().equals(item.getProduct().getId())) {
                existingItem.setQuantity(existingItem.getQuantity() + item.getQuantity());
                this.updatedAt = LocalDateTime.now();
                return;
            }
        }
        items.add(item);
        this.updatedAt = LocalDateTime.now();
    }

    public void removeItem(String productId) {
        items.removeIf(item -> item.getProduct().getId().equals(productId));
        this.updatedAt = LocalDateTime.now();
    }

    public void updateItemQuantity(String productId, int quantity) {
        for (CartItem item : items) {
            if (item.getProduct().getId().equals(productId)) {
                if (quantity <= 0) {
                    removeItem(productId);
                } else {
                    item.setQuantity(quantity);
                    this.updatedAt = LocalDateTime.now();
                }
                return;
            }
        }
    }

    public void clear() {
        items.clear();
        this.updatedAt = LocalDateTime.now();
    }
}
