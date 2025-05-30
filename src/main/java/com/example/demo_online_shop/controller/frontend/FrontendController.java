package com.example.demo_online_shop.controller.frontend;

import com.example.demo_online_shop.model.Category;
import com.example.demo_online_shop.model.Product;
import com.example.demo_online_shop.model.Store;
import com.example.demo_online_shop.model.Customer;
import com.example.demo_online_shop.model.Purchase;
import com.example.demo_online_shop.service.CategoryService;
import com.example.demo_online_shop.service.ProductService;
import com.example.demo_online_shop.service.StoreService;
import com.example.demo_online_shop.service.CustomerService;
import com.example.demo_online_shop.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/frontend")
public class FrontendController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final StoreService storeService;
    private final CustomerService customerService;
    private final PurchaseService purchaseService;

    @Autowired
    public FrontendController(ProductService productService, CategoryService categoryService, StoreService storeService, CustomerService customerService, PurchaseService purchaseService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.storeService = storeService;
        this.customerService = customerService;
        this.purchaseService = purchaseService;
    }

    @GetMapping
    public String home() {
        return "frontend/index";
    }

    @GetMapping("/products")
    public String manageProducts(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("stores", storeService.getAllStores());
        model.addAttribute("product", new Product()); // For the form
        return "frontend/products";
    }

    @PostMapping("/products/add")
    public String addProduct(@ModelAttribute("product") Product product,
                             @RequestParam("category.id") Long categoryId,
                             @RequestParam(name = "stores", required = false) List<Long> storeIds) {
        Category category = categoryService.getCategoryById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + categoryId));
        product.setCategory(category);

        if (storeIds != null && !storeIds.isEmpty()) {
            Set<Store> stores = storeIds.stream()
                    .map(id -> storeService.getStoreById(id)
                            .orElseThrow(() -> new IllegalArgumentException("Invalid store Id:" + id)))
                    .collect(Collectors.toSet());
            product.setStores(stores);
        } else {
            product.setStores(new HashSet<>());
        }

        productService.createProduct(product);
        return "redirect:/frontend/products";
    }

    @GetMapping("/categories")
    public String manageCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("category", new Category()); // For the form
        return "frontend/categories";
    }

    @PostMapping("/categories/add")
    public String addCategory(@ModelAttribute("category") Category category) {
        categoryService.createCategory(category);
        return "redirect:/frontend/categories";
    }

    @GetMapping("/stores")
    public String manageStores(Model model) {
        model.addAttribute("stores", storeService.getAllStores());
        model.addAttribute("store", new Store()); // For the form
        return "frontend/stores";
    }

    @PostMapping("/stores/add")
    public String addStore(@ModelAttribute("store") Store store) {
        storeService.createStore(store);
        return "redirect:/frontend/stores";
    }

    @GetMapping("/customers")
    public String manageCustomers(Model model) {
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("customer", new Customer()); // For the form
        return "frontend/customers";
    }

    @PostMapping("/customers/add")
    public String addCustomer(@ModelAttribute("customer") Customer customer) {
        customerService.createCustomer(customer);
        return "redirect:/frontend/customers";
    }

    @GetMapping("/purchases")
    public String managePurchases(Model model) {
        model.addAttribute("purchases", purchaseService.getAllPurchases());
        model.addAttribute("customers", customerService.getAllCustomers());
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("purchase", new Purchase()); // For the form
        return "frontend/purchases";
    }

    @PostMapping("/purchases/add")
    public String addPurchase(@ModelAttribute("purchase") Purchase purchase,
                              @RequestParam("customer.id") Long customerId,
                              @RequestParam("product.id") Long productId) {
        // The purchase object from the form will have purchaseDate set.
        // We need to fetch the full Customer and Product entities.
        Customer customer = customerService.getCustomerById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid customer Id:" + customerId));
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + productId));

        purchase.setCustomer(customer);
        purchase.setProduct(product);

        // The registerPurchase method in CustomerService was originally designed to take IDs and date.
        // We can adapt or call a new method in PurchaseService.
        // For simplicity, let's assume PurchaseService has a method to save a Purchase object.
        // If not, we'd call customerService.registerPurchase(customerId, productId, purchase.getPurchaseDate());
        // and ensure that method handles the Purchase object creation and saving.

        // Let's assume PurchaseService has a createPurchase method
        purchaseService.createPurchase(purchase);
        return "redirect:/frontend/purchases";
    }
}