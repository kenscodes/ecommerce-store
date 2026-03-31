package com.ecommerce.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for adding items to cart
 */
public class AddToCartRequest {
    
    @NotBlank(message = "Product ID is required")
    private String productId;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    public AddToCartRequest() {}

    public AddToCartRequest(String productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
