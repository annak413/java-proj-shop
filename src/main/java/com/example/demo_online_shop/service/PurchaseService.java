package com.example.demo_online_shop.service;

//Реєстрацію покупки
//Перегляд деталей конкретної покупки
import com.example.demo_online_shop.model.Customer;
import com.example.demo_online_shop.model.Product;
import com.example.demo_online_shop.model.Purchase;
import com.example.demo_online_shop.repository.CustomerRepo;
import com.example.demo_online_shop.repository.ProductRepo;
import com.example.demo_online_shop.repository.PurchaseRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

@Service
public class PurchaseService {

    private final PurchaseRepo purchaseRepo;
    private final CustomerRepo customerRepo;
    private final ProductRepo productRepo;

    @Autowired
    public PurchaseService(PurchaseRepo purchaseRepo, CustomerRepo customerRepo, ProductRepo productRepo) {
        this.purchaseRepo = purchaseRepo;
        this.customerRepo = customerRepo;
        this.productRepo = productRepo;
    }

    /**
     * Реєструє покупку продукту покупцем.
     *
     * @param customerId   Ідентифікатор покупця.
     * @param productId    Ідентифікатор продукту.
     * @param purchaseDate Дата покупки.
     * @return Зареєстрована покупка.
     * @throws IllegalArgumentException якщо покупця або продукт не знайдено.
     */
    @Transactional
    public Purchase registerPurchase(Long customerId, Long productId, LocalDate purchaseDate) {
        Optional<Customer> customerOptional = customerRepo.findById(customerId);
        Optional<Product> productOptional = productRepo.findById(productId);

        if (customerOptional.isPresent() && productOptional.isPresent()) {
            Customer customer = customerOptional.get();
            Product product = productOptional.get();
            Purchase purchase = new Purchase(customer, product, purchaseDate);
            return purchaseRepo.save(purchase);
        } else {
            String errorMessage = !customerOptional.isPresent() ? "Покупця з ID " + customerId + " не знайдено." : "Продукт з ID " + productId + " не знайдено.";
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Отримує покупку за її ID.
     *
     * @param purchaseId Ідентифікатор покупки.
     * @return Optional, що містить покупку, якщо знайдено.
     */
    public Optional<Purchase> getPurchaseById(Long purchaseId) {
        return purchaseRepo.findById(purchaseId);
    }

    /**
     * Отримує список всіх покупок.
     *
     * @return Список всіх покупок.
     */
    public List<Purchase> getAllPurchases() {
        return purchaseRepo.findAll();
    }

    /**
     * Creates a new purchase record.
     * Assumes the Purchase object has Customer, Product, and PurchaseDate already set.
     *
     * @param purchase The purchase object to save.
     * @return The saved purchase object.
     */
    @Transactional
    public Purchase createPurchase(Purchase purchase) {
        // Ensure customer and product are not null if they are required by business logic before saving
        if (purchase.getCustomer() == null || purchase.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Customer cannot be null for a purchase.");
        }
        if (purchase.getProduct() == null || purchase.getProduct().getId() == null) {
            throw new IllegalArgumentException("Product cannot be null for a purchase.");
        }
        if (purchase.getPurchaseDate() == null) {
            throw new IllegalArgumentException("Purchase date cannot be null.");
        }
        // Optional: Add check for duplicate purchase if needed, similar to registerPurchase
        // boolean alreadyPurchased = purchaseRepo.existsByCustomerAndProductAndPurchaseDate(purchase.getCustomer(), purchase.getProduct(), purchase.getPurchaseDate());
        // if (alreadyPurchased) {
        //     throw new IllegalStateException("This product has already been purchased by this customer on this date.");
        // }
        return purchaseRepo.save(purchase);
    }
}
