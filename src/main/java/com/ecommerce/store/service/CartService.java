package com.ecommerce.store.service;

import com.ecommerce.store.dto.AddToCartRequest;
import com.ecommerce.store.dto.CartResponse;
import com.ecommerce.store.model.Cart;
import com.ecommerce.store.model.CartItem;
import com.ecommerce.store.model.Product;
import com.ecommerce.store.repository.InMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for cart operations
 */
@Service
public class CartService {

    @Autowired
    private InMemoryStore store;

    public CartResponse getCartByCustomerId(String customerId) {
        Cart cart = store.getCartByCustomerId(customerId);
        if (cart == null) {
            cart = new Cart(customerId);
            store.saveCart(cart);
        }
        return convertToResponse(cart);
    }

    public CartResponse addToCart(String customerId, AddToCartRequest request) {
        // Validate product exists
        Product product = store.getProduct(request.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + request.getProductId());
        }

        // Check stock availability
        if (product.getStockQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient stock for product: " + request.getProductId());
        }

        // Get or create cart
        Cart cart = store.getCartByCustomerId(customerId);
        if (cart == null) {
            cart = new Cart(customerId);
        }

        // Add item to cart
        CartItem cartItem = new CartItem(product, request.getQuantity());
        cart.addItem(cartItem);
        
        store.saveCart(cart);
        return convertToResponse(cart);
    }

    public CartResponse updateCartItem(String customerId, String productId, int quantity) {
        Cart cart = store.getCartByCustomerId(customerId);
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found for customer: " + customerId);
        }

        // Validate product exists
        Product product = store.getProduct(productId);
        if (product == null) {
            throw new IllegalArgumentException("Product not found: " + productId);
        }

        // Check stock availability if increasing quantity
        if (quantity > 0) {
            int currentQuantity = cart.getItems().stream()
                    .filter(item -> item.getProduct().getId().equals(productId))
                    .mapToInt(CartItem::getQuantity)
                    .sum();
            
            if (quantity > currentQuantity && product.getStockQuantity() < (quantity - currentQuantity)) {
                throw new IllegalArgumentException("Insufficient stock for product: " + productId);
            }
        }

        cart.updateItemQuantity(productId, quantity);
        store.saveCart(cart);
        return convertToResponse(cart);
    }

    public CartResponse removeFromCart(String customerId, String productId) {
        Cart cart = store.getCartByCustomerId(customerId);
        if (cart == null) {
            throw new IllegalArgumentException("Cart not found for customer: " + customerId);
        }

        cart.removeItem(productId);
        store.saveCart(cart);
        return convertToResponse(cart);
    }

    public void clearCart(String customerId) {
        Cart cart = store.getCartByCustomerId(customerId);
        if (cart != null) {
            cart.clear();
            store.saveCart(cart);
        }
    }

    private CartResponse convertToResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setCustomerId(cart.getCustomerId());
        response.setItems(cart.getItems());
        response.setTotal(cart.getTotal());
        response.setCreatedAt(cart.getCreatedAt());
        response.setUpdatedAt(cart.getUpdatedAt());
        return response;
    }
}
