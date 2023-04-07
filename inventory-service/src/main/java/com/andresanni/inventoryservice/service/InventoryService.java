package com.andresanni.inventoryservice.service;

import com.andresanni.inventoryservice.model.Inventory;
import com.andresanni.inventoryservice.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    @Transactional(readOnly = true)
    public boolean isInStock(String skuCode){
       Optional<Inventory> optionalInventory = inventoryRepository.findBySkuCode(skuCode);
       return optionalInventory.isPresent();
    }
}
