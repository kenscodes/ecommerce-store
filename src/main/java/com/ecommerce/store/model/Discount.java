package com.ecommerce.store.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Represents a discount coupon
 */
public class Discount {
    private String code;
    private BigDecimal percentage;
    private boolean used;
    private LocalDateTime createdAt;
    private LocalDateTime usedAt;

    public Discount() {
        this.createdAt = LocalDateTime.now();
        this.used = false;
    }

    public Discount(String code, BigDecimal percentage) {
        this();
        this.code = code;
        this.percentage = percentage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
        if (used) {
            this.usedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUsedAt() {
        return usedAt;
    }

    public void setUsedAt(LocalDateTime usedAt) {
        this.usedAt = usedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Discount discount = (Discount) o;
        return code != null ? code.equalsIgnoreCase(discount.code) : discount.code == null;
    }

    @Override
    public int hashCode() {
        return code != null ? code.toLowerCase().hashCode() : 0;
    }
}
