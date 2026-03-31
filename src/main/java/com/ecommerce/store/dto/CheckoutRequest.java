package com.ecommerce.store.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for checkout process
 */
public class CheckoutRequest {
    
    @NotBlank(message = "Customer ID is required")
    private String customerId;
    
    private String discountCode;

    public CheckoutRequest() {}

    public CheckoutRequest(String customerId, String discountCode) {
        this.customerId = customerId;
        this.discountCode = discountCode;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(String discountCode) {
        this.discountCode = discountCode;
    }
}
