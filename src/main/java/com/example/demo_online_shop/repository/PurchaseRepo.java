package com.example.demo_online_shop.repository;
import com.example.demo_online_shop.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PurchaseRepo extends JpaRepository<Purchase, Long> {
    List<Purchase> findByCustomerId(Long customerId);
}
