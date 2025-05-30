package com.example.demo_online_shop.service;

import com.example.demo_online_shop.model.Product;
import com.example.demo_online_shop.model.Store;
import com.example.demo_online_shop.repository.ProductRepo;
import com.example.demo_online_shop.repository.StoreRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StoreService {

    private final StoreRepo storeRepo;
    private final ProductRepo productRepo;

    @Autowired
    public StoreService(StoreRepo storeRepo, ProductRepo productRepo) {
        this.storeRepo = storeRepo;
        this.productRepo = productRepo;
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
     * Додає новий магазин.
     *
     * @param store Об'єкт магазину, який потрібно додати.
     * @return Створений магазин.
     */
    public Store createStore(Store store) {
        return storeRepo.save(store);
    }

    /**
     * Отримує магазин за його ID.
     *
     * @param storeId Ідентифікатор магазину.
     * @return Optional, що містить магазин, якщо знайдено.
     */
    public Optional<Store> getStoreById(Long storeId) {
        return storeRepo.findById(storeId);
    }

    /**
     * Отримує список всіх покупок.
     *
     * @return Список всіх покупок.
     */
    public List<Store> getAllStores() {
        return storeRepo.findAll();
    }
}
