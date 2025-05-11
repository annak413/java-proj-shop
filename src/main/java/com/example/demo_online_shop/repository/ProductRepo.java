package com.example.demo_online_shop.repository;

import com.example.demo_online_shop.model.Product;
import com.example.demo_online_shop.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    List<Product> findAllByCategory(Category category);
}
