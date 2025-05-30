package com.onlineshops.yourprojectname.controller;

import com.onlineshops.yourprojectname.dto.ProductDTO;
import com.onlineshops.yourprojectname.model.Product;
import com.onlineshops.yourprojectname.model.Store;
import com.onlineshops.yourprojectname.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping("/dto") // Endpoint for DTOs
    public List<ProductDTO> getAllProductsDto(
            @RequestParam(required = false) String sortByPrice) {
        if (sortByPrice != null) {
            return productService.getProductsSortedByPrice(sortByPrice);
        }
        return productService.getAllProducts();
    }

    @GetMapping("/dto/{id}") // Endpoint for DTO
    public ResponseEntity<ProductDTO> getProductByIdDto(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/dto") // Endpoint for DTO
    public ResponseEntity<?> createProductDto(@RequestBody ProductDTO productDto) {
        try {
            ProductDTO createdProduct = productService.createProduct(productDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/dto/{id}") // Endpoint for DTO
    public ResponseEntity<?> updateProductDto(@PathVariable Long id, @RequestBody ProductDTO productDetailsDto) {
        try {
            ProductDTO updatedProduct = productService.updateProduct(id, productDetailsDto);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/category/{categoryId}")
    public List<ProductDTO> getProductsByCategory(@PathVariable Long categoryId) {
        return productService.getProductsByCategoryId(categoryId);
    }

    @PostMapping("/{productId}/add-to-store/{storeId}")
    public ResponseEntity<Product> addProductToStore(@PathVariable Long productId, @PathVariable Long storeId) {
        try {
            Product updatedProduct = productService.addProductToStore(productId, storeId);
            return ResponseEntity.ok(updatedProduct);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); // Or more specific error response
        }
    }

    @GetMapping("/{productId}/stores")
    public List<Store> getStoresByProductId(@PathVariable Long productId) {
        return productService.getStoresByProductId(productId);
    }
}