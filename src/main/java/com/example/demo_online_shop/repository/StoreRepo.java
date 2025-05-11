package com.example.demo_online_shop.repository;
import com.example.demo_online_shop.model.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StoreRepo extends JpaRepository<Store, Long> {
    Store findByName(String name);
}