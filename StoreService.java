package com.onlineshops.yourprojectname.service;

import com.onlineshops.yourprojectname.dto.StoreDTO;
import com.onlineshops.yourprojectname.model.Product;
import com.onlineshops.yourprojectname.model.Store;
import com.onlineshops.yourprojectname.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StoreService {
    @Autowired
    private StoreRepository storeRepository;

    public List<StoreDTO> getAllStores() {
        return storeRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public Optional<StoreDTO> getStoreById(Long id) {
        return storeRepository.findById(id)
                .map(this::convertToDto);
    }

    public StoreDTO createStore(Store store) {
        if (storeRepository.findByName(store.getName()).isPresent()) {
            throw new IllegalArgumentException("Store with this name already exists.");
        }
        return convertToDto(storeRepository.save(store));
    }

    public StoreDTO updateStore(Long id, Store storeDetails) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Store not found with id " + id));

        if (storeRepository.findByName(storeDetails.getName()).isPresent() &&
                !storeRepository.findByName(storeDetails.getName()).get().getId().equals(id)) {
            throw new IllegalArgumentException("Another store with this name already exists.");
        }

        store.setName(storeDetails.getName());
        store.setLocation(storeDetails.getLocation());
        return convertToDto(storeRepository.save(store));
    }

    public void deleteStore(Long id) {
        if (!storeRepository.existsById(id)) {
            throw new RuntimeException("Store not found with id " + id);
        }
        storeRepository.deleteById(id);
    }

    private StoreDTO convertToDto(Store store) {
        return new StoreDTO(store.getId(), store.getName(), store.getLocation());
    }

    // Helper method to retrieve store entity (for other services)
    public Optional<Store> getStoreEntityById(Long id) {
        return storeRepository.findById(id);
    }
}