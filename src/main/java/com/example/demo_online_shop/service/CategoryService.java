package com.example.demo_online_shop.service;

import com.example.demo_online_shop.model.Category;
import com.example.demo_online_shop.model.Product;
import com.example.demo_online_shop.repository.CategoryRepo;
import com.example.demo_online_shop.repository.ProductRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
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
            category.addProduct(product);
            productRepo.save(product);
            categoryRepo.save(category);
            return product;
        } else {
            throw new IllegalArgumentException("Категорію з ID " + categoryId + " не знайдено.");
        }
    }

    /**
     * Видаляє продукт з поточної категорії та одразу ж призначає його до нової категорії.
     *
     * @param currentCategoryId Ідентифікатор поточної категорії, з якої потрібно видалити продукт.
     * @param productId         Ідентифікатор продукту, який потрібно перемістити.
     * @param newCategoryId     Ідентифікатор нової категорії, до якої потрібно прив'язати продукт.
     * @throws NoSuchElementException якщо поточну або нову категорію чи продукт не знайдено.
     * @throws IllegalArgumentException якщо продукт не належить до поточної категорії.
     */
    @Transactional
    public void removeProductFromCategoryAndAssignNew(Long currentCategoryId, Long productId, Long newCategoryId) {
        Optional<Category> currentCategoryOptional = categoryRepo.findById(currentCategoryId);
        Optional<Category> newCategoryOptional = categoryRepo.findById(newCategoryId);
        Optional<Product> productOptional = productRepo.findById(productId);

        if (currentCategoryOptional.isEmpty()) {
            throw new NoSuchElementException("Поточну категорію з ID " + currentCategoryId + " не знайдено.");
        }
        if (newCategoryOptional.isEmpty()) {
            throw new NoSuchElementException("Нову категорію з ID " + newCategoryId + " не знайдено.");
        }
        if (productOptional.isEmpty()) {
            throw new NoSuchElementException("Продукт з ID " + productId + " не знайдено.");
        }

        Category currentCategory = currentCategoryOptional.get();
        Category newCategory = newCategoryOptional.get();
        Product productToRemove = productOptional.get();

        if (!currentCategory.getProducts().contains(productToRemove)) {
            throw new IllegalArgumentException("Продукт з ID " + productId + " не належить до категорії з ID " + currentCategoryId + ".");
        }

        currentCategory.removeProduct(productToRemove); // Оновлюємо колекцію поточної категорії
        productToRemove.setCategory(newCategory);       // Встановлюємо нову категорію для продукту
        newCategory.addProduct(productToRemove);       // Додаємо продукт до колекції нової категорії

        categoryRepo.save(currentCategory); // Зберігаємо зміни в поточній категорії
        categoryRepo.save(newCategory);     // Зберігаємо зміни в новій категорії
        productRepo.save(productToRemove);   // Зберігаємо зміни в продукті
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