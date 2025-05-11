package com.example.demo_online_shop.service;

//Включає метод для додавання продукту до магазину, а також методи для отримання продуктів певної
// категорії та магазинів, де доступний продукт
import com.example.demo_online_shop.model.Category;
import com.example.demo_online_shop.model.Product;
import com.example.demo_online_shop.model.Store;
import com.example.demo_online_shop.repository.CategoryRepo;
import com.example.demo_online_shop.repository.ProductRepo;
import com.example.demo_online_shop.repository.StoreRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final StoreRepo storeRepo;

    @Autowired
    public ProductService(ProductRepo productRepo, CategoryRepo categoryRepo, StoreRepo storeRepo) {
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.storeRepo = storeRepo;
    }

    /**
     * Додає продукт у магазин.
     *
     * @param productId Ідентифікатор продукту.
     * @param storeId   Ідентифікатор магазину.
     * @throws IllegalArgumentException якщо продукт або магазин не знайдено.
     */
    @Transactional
    public void addProductToStore(Long productId, Long storeId) {
        Optional<Product> productOptional = productRepo.findById(productId);
        Optional<Store> storeOptional = storeRepo.findById(storeId);

        if (productOptional.isPresent() && storeOptional.isPresent()) {
            Product product = productOptional.get();
            Store store = storeOptional.get();
            product.addStore(store);
            storeRepo.save(store); // Збереження магазину оновиться зв'язок
        } else {
            String errorMessage = !productOptional.isPresent() ? "Продукт з ID " + productId + " не знайдено." : "Магазин з ID " + storeId + " не знайдено.";
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Отримує список продуктів певної категорії.
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
     * Знаходить всі магазини, де доступний конкретний продукт.
     *
     * @param productId Ідентифікатор продукту.
     * @return Список магазинів, де доступний продукт.
     * @throws IllegalArgumentException якщо продукт не знайдено.
     */
    public List<Store> getStoresByProduct(Long productId) {
        Optional<Product> productOptional = productRepo.findById(productId);
        if (productOptional.isPresent()) {
            return new ArrayList<>(productOptional.get().getStores());
        } else {
            throw new IllegalArgumentException("Продукт з ID " + productId + " не знайдено.");
        }
    }

    /**
     * Додає новий продукт.
     *
     * @param product Об'єкт продукту, який потрібно додати.
     * @return Створений продукт.
     */
    public Product addProduct(Product product) {
        return productRepo.save(product);
    }

    /**
     * Отримує продукт за його ID.
     *
     * @param productId Ідентифікатор продукту.
     * @return Optional, що містить продукт, якщо знайдено.
     */
    public Optional<Product> getProductById(Long productId) {
        return productRepo.findById(productId);
    }
    /**
     * Отримує список всіх продуктів.
     *
     * @return Список всіх продуктів.
     */
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }
}
