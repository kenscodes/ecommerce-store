package com.ecommerce.store.controller;

import com.ecommerce.store.dto.ApiResponse;
import com.ecommerce.store.dto.CheckoutRequest;
import com.ecommerce.store.model.Order;
import com.ecommerce.store.service.CheckoutService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for checkout operations
 */
@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "*")
public class CheckoutController {

    @Autowired
    private CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<ApiResponse<Order>> checkout(@Valid @RequestBody CheckoutRequest request) {
        try {
            Order order = checkoutService.checkout(request);
            String message = "Order placed successfully";
            if (order.getDiscountCode() != null) {
                message += " with discount applied";
            }
            return ResponseEntity.ok(ApiResponse.success(message, order));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to process checkout: " + e.getMessage()));
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<Order>> getOrder(@PathVariable String orderId) {
        try {
            Order order = checkoutService.getOrder(orderId);
            return ResponseEntity.ok(ApiResponse.success(order));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve order: " + e.getMessage()));
        }
    }
}
