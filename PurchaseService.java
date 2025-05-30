package com.onlineshops.yourprojectname.service;

import com.onlineshops.yourprojectname.dto.PurchaseDTO;
import com.onlineshops.yourprojectname.model.Customer;
import com.onlineshops.yourprojectname.model.Product;
import com.onlineshops.yourprojectname.model.Purchase;
import com.onlineshops.yourprojectname.repository.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PurchaseService {
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ProductService productService;
    @Autowired
    private StoreService storeService; // To check if product is in store

    private PurchaseDTO convertToDto(Purchase purchase) {
        return new PurchaseDTO(
                purchase.getId(),
                purchase.getCustomer().getId(),
                purchase.getCustomer().getName(),
                purchase.getProduct().getId(),
                purchase.getProduct().getName(),
                purchase.getProduct().getPrice(),
                purchase.getPurchaseDate()
        );
    }

    public List<PurchaseDTO> getAllPurchases() {
        return purchaseRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<PurchaseDTO> getPurchaseById(Long id) {
        return purchaseRepository.findById(id)
                .map(this::convertToDto);
    }

    // Register a new purchase
    public PurchaseDTO registerPurchase(Long customerId, Long productId, Long storeId) {
        Customer customer = customerService.getCustomerEntityById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + customerId));
        Product product = productService.getProductEntityById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id " + productId));

        // Check if product is available in the specified store
        boolean isProductInStore = storeService.getStoreEntityById(storeId)
                .map(store -> store.getProducts().contains(product))
                .orElse(false);

        if (!isProductInStore) {
            throw new IllegalArgumentException("Product is not available in the specified store.");
        }

        Purchase purchase = new Purchase();
        purchase.setCustomer(customer);
        purchase.setProduct(product);
        purchase.setPurchaseDate(LocalDateTime.now()); // Set current purchase date

        return convertToDto(purchaseRepository.save(purchase));
    }

    // Get all purchases by a specific customer
    public List<PurchaseDTO> getPurchasesByCustomerId(Long customerId) {
        if (!customerService.getCustomerEntityById(customerId).isPresent()) {
            throw new RuntimeException("Customer not found with id " + customerId);
        }
        return purchaseRepository.findByCustomerId(customerId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Get purchases by customer sorted by date
    public List<PurchaseDTO> getPurchasesByCustomerIdSortedByDate(Long customerId, String order) {
        List<Purchase> purchases;
        if ("asc".equalsIgnoreCase(order)) {
            purchases = purchaseRepository.findByCustomerIdOrderByPurchaseDateAsc(customerId);
        } else if ("desc".equalsIgnoreCase(order)) {
            purchases = purchaseRepository.findByCustomerIdOrderByPurchaseDateDesc(customerId);
        } else {
            purchases = purchaseRepository.findByCustomerId(customerId); // Default to unsorted if order invalid
        }
        return purchases.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // TODO: Implement sorting by total sum if needed (requires more complex query/logic)

    public void deletePurchase(Long id) {
        if (!purchaseRepository.existsById(id)) {
            throw new RuntimeException("Purchase not found with id " + id);
        }
        purchaseRepository.deleteById(id);
    }
}