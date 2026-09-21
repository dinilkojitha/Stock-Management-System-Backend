package com.example.stockmanagementsystembackend.domain.inventory.service;

import com.example.stockmanagementsystembackend.domain.inventory.entity.Category;
import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.entity.UnitType;
import com.example.stockmanagementsystembackend.domain.inventory.repository.CategoryRepository;
import com.example.stockmanagementsystembackend.domain.inventory.repository.InventoryItemRepository;
import com.example.stockmanagementsystembackend.domain.inventory.repository.UnitTypeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
// Polymorphism allows this implementation to be referenced by the shared CRUD type.
public class InventoryItemService implements CrudService<InventoryItem, Integer> {
    private final InventoryItemRepository inventoryItemRepository;
    private final CategoryRepository categoryRepository;
    private final UnitTypeRepository unitTypeRepository;

    public InventoryItemService(
            InventoryItemRepository inventoryItemRepository,
            CategoryRepository categoryRepository,
            UnitTypeRepository unitTypeRepository
    ) {
        this.inventoryItemRepository = inventoryItemRepository;
        this.categoryRepository = categoryRepository;
        this.unitTypeRepository = unitTypeRepository;
    }

    @Override
    @Transactional
    public InventoryItem create(InventoryItem inventoryItem) {
        validateInventoryItem(inventoryItem);
        inventoryItem.setId(null);
        attachReferences(inventoryItem);
        return inventoryItemRepository.save(inventoryItem);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InventoryItem> getAll() {
        return inventoryItemRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public InventoryItem getById(Integer id) {
        return inventoryItemRepository.findById(id)
                .orElseThrow(() -> inventoryItemNotFound(id));
    }

    @Override
    @Transactional
    public InventoryItem update(Integer id, InventoryItem inventoryItem) {
        validateInventoryItem(inventoryItem);
        attachReferences(inventoryItem);

        InventoryItem existingItem = inventoryItemRepository.findById(id)
                .orElseThrow(() -> inventoryItemNotFound(id));
        existingItem.setName(inventoryItem.getName());
        existingItem.setCategory(inventoryItem.getCategory());
        existingItem.setTotalQuantity(inventoryItem.getTotalQuantity());
        existingItem.setUnitType(inventoryItem.getUnitType());
        existingItem.setUnitPrice(inventoryItem.getUnitPrice());
        existingItem.setDescription(inventoryItem.getDescription());
        existingItem.setReorderThreshold(inventoryItem.getReorderThreshold());

        return inventoryItemRepository.save(existingItem);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        InventoryItem inventoryItem = inventoryItemRepository.findById(id)
                .orElseThrow(() -> inventoryItemNotFound(id));

        try {
            inventoryItemRepository.delete(inventoryItem);
            inventoryItemRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Inventory item cannot be deleted because it is in use"
            );
        }
    }

    private void validateInventoryItem(InventoryItem inventoryItem) {
        if (inventoryItem == null || inventoryItem.getName() == null || inventoryItem.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name must not be blank");
        }
        if (inventoryItem.getName().length() > 60) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name must not exceed 60 characters");
        }
        if (inventoryItem.getCategory() == null || inventoryItem.getCategory().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "category.id is required");
        }
        if (inventoryItem.getUnitType() == null || inventoryItem.getUnitType().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unitType.id is required");
        }
    }

    private void attachReferences(InventoryItem inventoryItem) {
        Integer categoryId = inventoryItem.getCategory().getId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Category with ID " + categoryId + " was not found"
                ));

        Integer unitTypeId = inventoryItem.getUnitType().getId();
        UnitType unitType = unitTypeRepository.findById(unitTypeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Unit type with ID " + unitTypeId + " was not found"
                ));

        inventoryItem.setCategory(category);
        inventoryItem.setUnitType(unitType);
    }

    private ResponseStatusException inventoryItemNotFound(Integer id) {
        return new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Inventory item with ID " + id + " was not found"
        );
    }
}
