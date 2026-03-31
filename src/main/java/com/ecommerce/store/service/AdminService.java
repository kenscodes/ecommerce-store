package com.ecommerce.store.service;

import com.ecommerce.store.model.Discount;
import com.ecommerce.store.repository.InMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for admin operations
 */
@Service
public class AdminService {

    @Autowired
    private InMemoryStore store;

    public Discount generateDiscountCode() {
        Discount discount = store.generateDiscountForOrder();
        if (discount == null) {
            throw new IllegalStateException("No discount code generated. Current order count does not meet the criteria.");
        }
        return discount;
    }

    public Map<String, Object> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        
        analytics.put("totalOrders", store.getTotalOrders());
        analytics.put("totalItemsSold", store.getTotalItemsSold());
        analytics.put("totalRevenue", store.getTotalRevenue());
        analytics.put("totalDiscountCodes", store.getTotalDiscountCodes());
        analytics.put("usedDiscountCodes", store.getUsedDiscountCodes());
        analytics.put("totalDiscountGiven", store.getTotalDiscountGiven());
        analytics.put("discountEveryNthOrder", store.getDiscountEveryNthOrder());
        analytics.put("discountPercentage", store.getDiscountPercentage());
        
        return analytics;
    }

    public List<Discount> getAllDiscounts() {
        return store.getAllDiscounts();
    }

    public void updateDiscountSettings(int everyNthOrder, BigDecimal percentage) {
        if (everyNthOrder <= 0) {
            throw new IllegalArgumentException("Every Nth order must be greater than 0");
        }
        if (percentage.compareTo(BigDecimal.ZERO) <= 0 || percentage.compareTo(BigDecimal.valueOf(100)) >= 0) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }
        
        store.setDiscountEveryNthOrder(everyNthOrder);
        store.setDiscountPercentage(percentage);
    }
}
