package com.onlineshops.yourprojectname.controller;

import com.onlineshops.yourprojectname.dto.PurchaseDTO;
import com.onlineshops.yourprojectname.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public List<PurchaseDTO> getAllPurchases() {
        return purchaseService.getAllPurchases();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PurchaseDTO> getPurchaseById(@PathVariable Long id) {
        return purchaseService.getPurchaseById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping // Expects JSON like: { "customerId": 1, "productId": 1, "storeId": 1 }
    public ResponseEntity<?> registerPurchase(@RequestBody Map<String, Long> payload) {
        Long customerId = payload.get("customerId");
        Long productId = payload.get("productId");
        Long storeId = payload.get("storeId"); // Optional: for checking product availability in store

        if (customerId == null || productId == null || storeId == null) {
            return ResponseEntity.badRequest().body("Customer ID, Product ID, and Store ID are required.");
        }

        try {
            PurchaseDTO purchase = purchaseService.registerPurchase(customerId, productId, storeId);
            return ResponseEntity.status(HttpStatus.CREATED).body(purchase);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePurchase(@PathVariable Long id) {
        try {
            purchaseService.deletePurchase(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/customer/{customerId}")
    public List<PurchaseDTO> getPurchasesByCustomerId(
            @PathVariable Long customerId,
            @RequestParam(required = false) String sortByDate) {
        if (sortByDate != null) {
            return purchaseService.getPurchasesByCustomerIdSortedByDate(customerId, sortByDate);
        }
        return purchaseService.getPurchasesByCustomerId(customerId);
    }
}