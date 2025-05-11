package com.example.demo_online_shop.controller;

import com.example.demo_online_shop.model.Category;
import com.example.demo_online_shop.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public Optional<Category> getCategoryById(@PathVariable("id") Long id) {
        return categoryService.getCategoryById(id);
    }

    @PutMapping("/{currentCategoryId}/products/{productId}/assign/{newCategoryId}")
    public ResponseEntity<?> moveProductToNewCategory(
            @PathVariable Long currentCategoryId,
            @PathVariable Long productId,
            @PathVariable Long newCategoryId) {
        try {
            categoryService.removeProductFromCategoryAndAssignNew(currentCategoryId, productId, newCategoryId);
            return ResponseEntity.ok().build();
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Виникла помилка при переміщенні продукту.");
        }
    }
}