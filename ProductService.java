package com.onlineshops.yourprojectname.service;

import com.onlineshops.yourprojectname.dto.ProductDTO;
import com.onlineshops.yourprojectname.model.Category;
import com.onlineshops.yourprojectname.model.Product;
import com.onlineshops.yourprojectname.model.Store;
import com.onlineshops.yourprojectname.repository.CategoryRepository;
import com.onlineshops.yourprojectname.repository.ProductRepository;
import com.onlineshops.yourprojectname.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private StoreRepository storeRepository;

    // Convert Product entity to ProductDTO
    private ProductDTO convertToDto(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(product.getPrice());
        dto.setProducer(product.getProducer());
        dto.setCountryOfOrigin(product.getCountryOfOrigin());
        dto.setWeight(product.getWeight());
        dto.setDescription(product.getDescription());
        dto.setImageUrls(product.getImageUrls());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        return dto;
    }

    // Convert ProductDTO to Product entity (for creation/update)
    private Product convertToEntity(ProductDTO productDto) {
        Product product = new Product();
        product.setId(productDto.getId()); // ID will be null for new products
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());
        product.setProducer(productDto.getProducer());
        product.setCountryOfOrigin(productDto.getCountryOfOrigin());
        product.setWeight(productDto.getWeight());
        product.setDescription(productDto.getDescription());
        product.setImageUrls(productDto.getImageUrls());

        if (productDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id " + productDto.getCategoryId()));
            product.setCategory(category);
        }
        return product;
    }

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<ProductDTO> getProductById(Long id) {
        return productRepository.findById(id)
                .map(this::convertToDto);
    }

    public ProductDTO createProduct(ProductDTO productDto) {
        Product product = convertToEntity(productDto);
        return convertToDto(productRepository.save(product));
    }

    public ProductDTO updateProduct(Long id, ProductDTO productDetailsDto) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + id));

        existingProduct.setName(productDetailsDto.getName());
        existingProduct.setPrice(productDetailsDto.getPrice());
        existingProduct.setProducer(productDetailsDto.getProducer());
        existingProduct.setCountryOfOrigin(productDetailsDto.getCountryOfOrigin());
        existingProduct.setWeight(productDetailsDto.getWeight());
        existingProduct.setDescription(productDetailsDto.getDescription());
        existingProduct.setImageUrls(productDetailsDto.getImageUrls());

        if (productDetailsDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productDetailsDto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id " + productDetailsDto.getCategoryId()));
            existingProduct.setCategory(category);
        } else {
            existingProduct.setCategory(null); // Allow removing category
        }

        return convertToDto(productRepository.save(existingProduct));
    }

    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Product not found with id " + id);
        }
        productRepository.deleteById(id);
    }

    // Functional Requirements

    // Get products by category
    public List<ProductDTO> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Add product to a store
    public Product addProductToStore(Long productId, Long storeId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + productId));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new RuntimeException("Store not found with id " + storeId));

        if (!store.getProducts().contains(product)) {
            store.getProducts().add(product);
            product.getStores().add(store); // Ensure bidirectional relationship is maintained
            storeRepository.save(store); // Save the store to update the ManyToMany relationship
        }
        return product;
    }

    // Find all stores where a specific product is available
    public List<Store> getStoresByProductId(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found with id " + productId);
        }
        return storeRepository.findByProducts_Id(productId);
    }

    // Get products sorted by price
    public List<ProductDTO> getProductsSortedByPrice(String order) {
        List<Product> products;
        if ("asc".equalsIgnoreCase(order)) {
            products = productRepository.findByOrderByPriceAsc();
        } else if ("desc".equalsIgnoreCase(order)) {
            products = productRepository.findByOrderByPriceDesc();
        } else {
            products = productRepository.findAll(); // Default to unsorted
        }
        return products.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Helper method to retrieve product entity (for other services)
    public Optional<Product> getProductEntityById(Long id) {
        return productRepository.findById(id);
    }
}