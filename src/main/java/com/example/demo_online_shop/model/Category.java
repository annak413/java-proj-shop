// Category.java
package com.example.demo_online_shop.model;
import jakarta.persistence.*;
import java.util.Set;
import java.util.HashSet;
import java.util.Objects;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Product> products = new HashSet<>();

    public Category() {}

    public Category(String name) {
        this.name = name;
    }

    // Геттери та сеттери

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Product> getProducts() {
        return products;
    }

    public void setProducts(Set<Product> products) {
        this.products = products;
        for (Product product : products) {
            product.setCategory(this);
        }
    }

    public void addProduct(Product product) {
        if (product != null && products.add(product)) {
            product.setCategory(this);
        }
    }

    public void removeProduct(Product product) {
        if (product != null && products.contains(product)) {
            product.setCategory(null); // Встановлюємо категорію в null для об'єктної моделі
            products.remove(product);
            // Логіку збереження продукту з новою категорією або видалення
            // слід перенести до сервісного шару.
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return id != null && id.equals(category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}