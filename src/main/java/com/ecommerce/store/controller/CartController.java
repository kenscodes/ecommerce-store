package com.ecommerce.store.controller;

import com.ecommerce.store.dto.AddToCartRequest;
import com.ecommerce.store.dto.ApiResponse;
import com.ecommerce.store.dto.CartResponse;
import com.ecommerce.store.service.CartService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for cart operations
 */
@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@PathVariable String customerId) {
        try {
            CartResponse cart = cartService.getCartByCustomerId(customerId);
            return ResponseEntity.ok(ApiResponse.success(cart));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve cart: " + e.getMessage()));
        }
    }

    @PostMapping("/{customerId}/items")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @PathVariable String customerId,
            @Valid @RequestBody AddToCartRequest request) {
        try {
            CartResponse cart = cartService.addToCart(customerId, request);
            return ResponseEntity.ok(ApiResponse.success("Item added to cart successfully", cart));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to add item to cart: " + e.getMessage()));
        }
    }

    @PutMapping("/{customerId}/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @PathVariable String customerId,
            @PathVariable String productId,
            @RequestParam int quantity) {
        try {
            CartResponse cart = cartService.updateCartItem(customerId, productId, quantity);
            return ResponseEntity.ok(ApiResponse.success("Cart item updated successfully", cart));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update cart item: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{customerId}/items/{productId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeFromCart(
            @PathVariable String customerId,
            @PathVariable String productId) {
        try {
            CartResponse cart = cartService.removeFromCart(customerId, productId);
            return ResponseEntity.ok(ApiResponse.success("Item removed from cart successfully", cart));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to remove item from cart: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{customerId}")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable String customerId) {
        try {
            cartService.clearCart(customerId);
            return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to clear cart: " + e.getMessage()));
        }
    }
}
