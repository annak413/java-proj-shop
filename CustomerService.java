package com.onlineshops.yourprojectname.service;

import com.onlineshops.yourprojectname.dto.CustomerDTO;
import com.onlineshops.yourprojectname.model.Customer;
import com.onlineshops.yourprojectname.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<CustomerDTO> getCustomerById(Long id) {
        return customerRepository.findById(id)
                .map(this::convertToDto);
    }

    public CustomerDTO createCustomer(Customer customer) {
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Customer with this email already exists.");
        }
        // In a real application, you'd hash the password here before saving
        return convertToDto(customerRepository.save(customer));
    }

    public CustomerDTO updateCustomer(Long id, Customer customerDetails) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found with id " + id));

        if (customerRepository.findByEmail(customerDetails.getEmail()).isPresent() &&
                !customerRepository.findByEmail(customerDetails.getEmail()).get().getId().equals(id)) {
            throw new IllegalArgumentException("Another customer with this email already exists.");
        }

        customer.setName(customerDetails.getName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhoneNumber(customerDetails.getPhoneNumber());
        // Handle password update separately or with care, maybe dedicated endpoint
        if (customerDetails.getPassword() != null && !customerDetails.getPassword().isEmpty()) {
            customer.setPassword(customerDetails.getPassword()); // Hash this in real app!
        }

        return convertToDto(customerRepository.save(customer));
    }

    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("Customer not found with id " + id);
        }
        customerRepository.deleteById(id);
    }

    private CustomerDTO convertToDto(Customer customer) {
        return new CustomerDTO(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhoneNumber()
        );
    }

    // Helper method to retrieve customer entity (for other services)
    public Optional<Customer> getCustomerEntityById(Long id) {
        return customerRepository.findById(id);
    }
}