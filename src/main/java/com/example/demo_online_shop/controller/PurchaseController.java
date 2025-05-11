package com.example.demo_online_shop.controller;

import com.example.demo_online_shop.model.Purchase;
import com.example.demo_online_shop.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;

    @GetMapping
    public List<Purchase> getAllPurchases() {
        return purchaseService.getAllPurchases();
    }

    @GetMapping("/{id}")
    public Optional<Purchase> getPurchaseById(@PathVariable("id") Long id) {
        return purchaseService.getPurchaseById(id);
    }
}
