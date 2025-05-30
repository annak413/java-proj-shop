# 🛒 Task: Shopping Management System for Stores

## 🎯 Goal
Implement a Java application using Hibernate that models a simple system for purchasing products in stores.

## 📚 Entity Descriptions

### 🧺 Product
- `id` - Unique identifier
- `name` - Product name
- `price` - Price
- 🔗 Belongs to **one category**
- 🔗 Sold in **many stores**

### 🗂 Category
- `id` - Unique identifier
- `name` - Category name
- 🔗 Has **many products**

### 🏬 Store
- `id` - Unique identifier
- `name` - Store name
- `location` - Location
- 🔗 Sells **many products**

### 🧑‍💼 Customer
- `id` - Unique identifier
- `name` - Customer name
- `email` - Email
- 🔗 Can purchase **many products**

### 🧾 Purchase
Additional entity storing purchase information:
- `id`
- `customer`
- `product`
- `purchaseDate`

## 🔗 Entity Relationships
- `Product` → `Category`: `ManyToOne`
- `Category` → `Product`: `OneToMany`
- `Product` ↔ `Store`: `ManyToMany`
- `Customer` ↔ `Product`: `ManyToMany` through `Purchase`

## ✅ Functional Requirements
- Add new product to a category
- Add product to a store
- Register product purchase by customer
- Get list of products in a specific category
- Find all stores where a specific product is available
- View all products purchased by a specific customer

## 🌟 Additional (Optional) Features
- Store purchase date
- Check product availability in store before purchase
- Add uniqueness constraints (e.g., `email` for customer, `name` for store)
- Add filtering and sorting:
    - Product list by price (ascending/descending)
    - Customer purchases by date or total amount

## 🛠 Technologies
- Java
- Spring Boot
- Hibernate (JPA)
- H2/PostgreSQL (choice)
- Maven

---

> 💡 Recommended implementation using layered architecture: `model`, `repository`, `service`, `controller`.
