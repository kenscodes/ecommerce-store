package com.ecommerce.store.model;

import java.math.BigDecimal;

/**
 * Represents an item in a shopping cart
 */
public class CartItem {
    private Product product;
    private int quantity;

    public CartItem() {}

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return product != null ? product.getId().equals(cartItem.product.getId()) : cartItem.product == null;
    }

    @Override
    public int hashCode() {
        return product != null ? product.getId().hashCode() : 0;
    }
}
