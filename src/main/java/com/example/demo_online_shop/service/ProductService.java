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
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
    /**
     * Creates a new product and establishes its relationships with categories and stores.
     * The method assumes that the input 'product' object has its name, price,
     * a managed Category entity, and a Set of managed Store entities already set by the caller.
     *
     * @param product The product object to be created.
     * @return The persisted product entity with its ID and relationships.
     */
    @Transactional
    public Product createProduct(Product product) {
        // The 'product' parameter comes from the FrontendController.
        // It should have:
        // - Name and Price set.
        // - A managed Category object set via product.setCategory().
        // - A Set of managed Store objects set via product.setStores().

        // Extract the stores to be associated. These are managed entities.
        Set<Store> storesToAssociate = product.getStores() != null ? new HashSet<>(product.getStores()) : new HashSet<>();

        // Clear the stores collection on the incoming product instance before saving it.
        // This is a good practice to ensure that Hibernate correctly manages the relationship
        // from the owning side (Store) when the product is saved.
        product.setStores(new HashSet<>());

        // Save the Product entity (persists name, price, and the ManyToOne Category relationship).
        // The 'savedProduct' will have an ID generated by the database.
        Product savedProduct = productRepo.save(product);

        // Handle the ManyToMany relationship with Stores.
        // 'Store' is the owning side of the relationship (Product.stores is mappedBy "products" in Store).
        // Therefore, we must add the 'savedProduct' to each associated Store's collection of products
        // and then persist these changes by saving each Store.
        if (!storesToAssociate.isEmpty()) {
            for (Store store : storesToAssociate) {
                // 'store' is a managed entity (loaded by StoreService in the FrontendController).
                // Add the 'savedProduct' to this store's collection of products.
                store.getProducts().add(savedProduct);
                // storeRepo.save(store); // Persist the change to the Store entity. Can be done in a batch after the loop.

                // For consistency in the object graph within the current transaction,
                // also add the store to the 'savedProduct's' collection of stores.
                // This does not drive the database change (due to mappedBy) but keeps the Java objects in sync.
                savedProduct.getStores().add(store);
            }

            // After updating all necessary Store entities in memory, save them to persist the relationship changes.
            for (Store store : storesToAssociate) {
                storeRepo.save(store);
            }
        }

        return savedProduct; // Return the persisted product.
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
