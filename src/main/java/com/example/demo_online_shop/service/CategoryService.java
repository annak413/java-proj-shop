package com.example.demo_online_shop.service;

import com.example.demo_online_shop.model.Category;
import com.example.demo_online_shop.model.Product;
import com.example.demo_online_shop.repository.CategoryRepo;
import com.example.demo_online_shop.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepo categoryRepo;
    private final ProductRepo productRepo;

    @Autowired
    public CategoryService(CategoryRepo categoryRepo, ProductRepo productRepo) {
        this.categoryRepo = categoryRepo;
        this.productRepo = productRepo;
    }

    /**
     * Додає новий продукт до вказаної категорії.
     *
     * @param categoryId Ідентифікатор категорії.
     * @param product    Об'єкт продукту, який потрібно додати.
     * @return Створений продукт.
     * @throws IllegalArgumentException якщо категорію не знайдено.
     */
    @Transactional
    public Product addNewProductToCategory(Long categoryId, Product product) {
        Optional<Category> categoryOptional = categoryRepo.findById(categoryId);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            product.setCategory(category);
            category.addProduct(product); // Додано виклик для оновлення зв'язку з боку Category
            return productRepo.save(product);
        } else {
            throw new IllegalArgumentException("Категорію з ID " + categoryId + " не знайдено.");
        }
    }

    /**
     * Отримує список всіх продуктів, що належать до певної категорії.
     *
     * @param categoryId Ідентифікатор категорії.
     * @return Список продуктів даної категорії.
     * @throws IllegalArgumentException якщо категорію не знайдено.
     */
    public List<Product> getProductsByCategory(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepo.findById(categoryId);
        if (categoryOptional.isPresent()) {
            return productRepo.findAllByCategory(categoryOptional.get());
        } else {
            throw new IllegalArgumentException("Категорію з ID " + categoryId + " не знайдено.");
        }
    }

    /**
     * Додає нову категорію.
     *
     * @param category Об'єкт категорії, яку потрібно додати.
     * @return Створена категорія.
     */
    public Category addCategory(Category category) {
        return categoryRepo.save(category);
    }

    /**
     * Отримує категорію за її ID.
     *
     * @param categoryId Ідентифікатор категорії.
     * @return Optional, що містить категорію, якщо знайдено.
     */
    public Optional<Category> getCategoryById(Long categoryId) {
        return categoryRepo.findById(categoryId);
    }

    /**
     * Отримує список всіх категорій.
     *
     * @return Список всіх категорій.
     */
    public List<Category> getAllCategories() {
        return categoryRepo.findAll();
    }
}
