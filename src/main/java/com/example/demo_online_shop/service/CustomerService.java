package com.example.demo_online_shop.service;

//Реєстрацію покупки продукту покупцем
//Перегляд всіх продуктів, які купив певний покупець

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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepo customerRepo;
    private final ProductRepo productRepo;
    private final PurchaseRepo purchaseRepo;

    @Autowired
    public CustomerService(CustomerRepo customerRepo, ProductRepo productRepo, PurchaseRepo purchaseRepo) {
        this.customerRepo = customerRepo;
        this.productRepo = productRepo;
        this.purchaseRepo = purchaseRepo;
    }

    /**
     * Реєструє покупку продукту покупцем.
     *
     * @param customerId Ідентифікатор покупця.
     * @param productId  Ідентифікатор продукту.
     * @param purchaseDate Дата покупки.
     * @throws IllegalArgumentException якщо покупця або продукт не знайдено.
     */
    @Transactional
    public void registerPurchase(Long customerId, Long productId, LocalDate purchaseDate) {
        Optional<Customer> customerOptional = customerRepo.findById(customerId);
        Optional<Product> productOptional = productRepo.findById(productId);

        if (customerOptional.isPresent() && productOptional.isPresent()) {
            Customer customer = customerOptional.get();
            Product product = productOptional.get();

            boolean alreadyPurchased = purchaseRepo.existsByCustomerAndProductAndPurchaseDate(customer, product, purchaseDate);
            if (alreadyPurchased) {
                throw new IllegalStateException("Цей товар уже був куплений цим покупцем у вказану дату.");
            }

            Purchase purchase = new Purchase(customer, product, purchaseDate);
            purchaseRepo.save(purchase);
        } else {
            String errorMessage = !customerOptional.isPresent()
                    ? "Покупця з ID " + customerId + " не знайдено."
                    : "Продукт з ID " + productId + " не знайдено.";
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Переглядає всі продукти, які купив певний покупець.
     *
     * @param customerId Ідентифікатор покупця.
     * @return Список продуктів, куплених покупцем.
     * @throws IllegalArgumentException якщо покупця не знайдено.
     */
    public List<Product> getPurchasedProductsByCustomer(Long customerId) {
        Optional<Customer> customerOptional = customerRepo.findById(customerId);
        if (customerOptional.isPresent()) {
            return purchaseRepo.findByCustomerId(customerId).stream()
                    .map(Purchase::getProduct)
                    .collect(Collectors.toList());
        } else {
            throw new IllegalArgumentException("Покупця з ID " + customerId + " не знайдено.");
        }
    }

    /**
     * Додає нового покупця.
     *
     * @param customer Об'єкт покупця, якого потрібно додати.
     * @return Створений покупець.
     */
    public Customer createCustomer(Customer customer) {
        return customerRepo.save(customer);
    }

    /**
     * Отримує покупця за його ID.
     *
     * @param customerId Ідентифікатор покупця.
     * @return Optional, що містить покупця, якщо знайдено.
     */
    public Optional<Customer> getCustomerById(Long customerId) {
        return customerRepo.findById(customerId);
    }

    /**
     * Отримує список всіх клієнтів.
     *
     * @return Список всіх клієнтів.
     */
    public List<Customer> getAllCustomers() {
        return customerRepo.findAll();
    }
}
