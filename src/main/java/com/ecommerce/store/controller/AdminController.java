package com.ecommerce.store.controller;

import com.ecommerce.store.dto.ApiResponse;
import com.ecommerce.store.model.Discount;
import com.ecommerce.store.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controller for admin operations
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @PostMapping("/discounts/generate")
    public ResponseEntity<ApiResponse<Discount>> generateDiscountCode() {
        try {
            Discount discount = adminService.generateDiscountCode();
            return ResponseEntity.ok(ApiResponse.success("Discount code generated successfully", discount));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to generate discount code: " + e.getMessage()));
        }
    }

    @GetMapping("/discounts")
    public ResponseEntity<ApiResponse<List<Discount>>> getAllDiscounts() {
        try {
            List<Discount> discounts = adminService.getAllDiscounts();
            return ResponseEntity.ok(ApiResponse.success(discounts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve discounts: " + e.getMessage()));
        }
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getAnalytics() {
        try {
            Map<String, Object> analytics = adminService.getAnalytics();
            return ResponseEntity.ok(ApiResponse.success(analytics));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve analytics: " + e.getMessage()));
        }
    }

    @PutMapping("/settings/discount")
    public ResponseEntity<ApiResponse<Void>> updateDiscountSettings(
            @RequestParam int everyNthOrder,
            @RequestParam BigDecimal percentage) {
        try {
            adminService.updateDiscountSettings(everyNthOrder, percentage);
            return ResponseEntity.ok(ApiResponse.success("Discount settings updated successfully", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to update discount settings: " + e.getMessage()));
        }
    }
}
