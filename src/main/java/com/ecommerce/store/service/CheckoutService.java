package com.ecommerce.store.service;

import com.ecommerce.store.dto.CheckoutRequest;
import com.ecommerce.store.model.*;
import com.ecommerce.store.repository.InMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service for checkout operations
 */
@Service
public class CheckoutService {

    @Autowired
    private InMemoryStore store;

    @Autowired
    private CartService cartService;

    public Order checkout(CheckoutRequest request) {
        // Get cart for the customer
        Cart cart = store.getCartByCustomerId(request.getCustomerId());
        if (cart == null || cart.getItems().isEmpty()) {
            throw new IllegalArgumentException("Cart is empty or not found");
        }

        // Create order
        Order order = new Order(request.getCustomerId(), cart.getItems());

        // Apply discount if provided
        if (request.getDiscountCode() != null && !request.getDiscountCode().trim().isEmpty()) {
            Discount discount = store.getDiscount(request.getDiscountCode());
            if (discount == null) {
                throw new IllegalArgumentException("Invalid discount code: " + request.getDiscountCode());
            }

            if (discount.isUsed()) {
                throw new IllegalArgumentException("Discount code already used: " + request.getDiscountCode());
            }

            // Apply discount
            order.applyDiscount(discount.getPercentage());
            order.setDiscountCode(discount.getCode());
            
            // Mark discount as used
            discount.setUsed(true);
            store.saveDiscount(discount);
        }

        // Update product stock
        for (CartItem item : cart.getItems()) {
            Product product = store.getProduct(item.getProduct().getId());
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getId());
            }
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
        }

        // Save order
        order.setStatus(Order.OrderStatus.CONFIRMED);
        store.saveOrder(order);

        // Check if this order qualifies for a new discount
        Discount newDiscount = store.generateDiscountForOrder();

        // Clear the cart
        cartService.clearCart(request.getCustomerId());

        return order;
    }

    public Order getOrder(String orderId) {
        Order order = store.getOrder(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        return order;
    }
}
