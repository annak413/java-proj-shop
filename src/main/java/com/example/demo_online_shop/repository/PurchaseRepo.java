package com.example.demo_online_shop.repository;
import com.example.demo_online_shop.model.Customer;
import com.example.demo_online_shop.model.Product;
import com.example.demo_online_shop.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PurchaseRepo extends JpaRepository<Purchase, Long> {
    List<Purchase> findByCustomerId(Long customerId);
    boolean existsByCustomerAndProductAndPurchaseDate(Customer customer, Product product, LocalDate purchaseDate);
}
