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
        return save(inventoryItem);
    }

    @Override
    @Transactional
    public InventoryItem save(InventoryItem inventoryItem) {
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

        InventoryItem existingItem = inventoryItemRepository.findByIdForUpdate(id)
                .orElseThrow(() -> inventoryItemNotFound(id));
        attachReferences(inventoryItem);
        existingItem.setName(inventoryItem.getName());
        existingItem.setCategory(inventoryItem.getCategory());
        existingItem.setTotalQuantity(inventoryItem.getTotalQuantity());
        existingItem.setUnitType(inventoryItem.getUnitType());
        existingItem.setUnitPrice(inventoryItem.getUnitPrice());
        existingItem.setDescription(inventoryItem.getDescription());
        existingItem.setReorderThreshold(inventoryItem.getReorderThreshold());

        return inventoryItemRepository.save(existingItem);
    }

    @Transactional(readOnly = true)
    public List<InventoryItem> search(String keyword) {
        return inventoryItemRepository.findByNameContainingIgnoreCaseOrderByNameAsc(
                keyword == null ? "" : keyword.strip());
    }

    @Transactional(readOnly = true)
    public List<InventoryItem> getByCategory(Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category was not found");
        }
        return inventoryItemRepository.findByCategory_CategoryIdOrderByNameAsc(categoryId);
    }

    @Transactional(readOnly = true)
    public List<InventoryItem> getLowStock() {
        return inventoryItemRepository.findLowStock();
    }

    @Transactional
    public InventoryItem adjustQuantity(Integer id, Double quantityDelta) {
        if (quantityDelta == null || !Double.isFinite(quantityDelta)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "quantityDelta must be a finite number");
        }
        InventoryItem item = inventoryItemRepository.findByIdForUpdate(id)
                .orElseThrow(() -> inventoryItemNotFound(id));
        double currentQuantity = item.getTotalQuantity() == null ? 0.0 : item.getTotalQuantity();
        double resultingQuantity = currentQuantity + quantityDelta;
        validateNonNegative("Resulting totalQuantity", resultingQuantity);
        item.setTotalQuantity(resultingQuantity);
        return inventoryItemRepository.save(item);
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
        inventoryItem.setName(inventoryItem.getName());
        if (inventoryItem.getName().length() > 60) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name must not exceed 60 characters");
        }
        if (inventoryItem.getCategoryId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "categoryId is required");
        }
        if (inventoryItem.getUnitTypeId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unitTypeId is required");
        }
        validateNonNegative("totalQuantity", inventoryItem.getTotalQuantity());
        validateNonNegative("unitPrice", inventoryItem.getUnitPrice());
        validateNonNegative("reorderThreshold", inventoryItem.getReorderThreshold());
    }

    private void validateNonNegative(String field, Double value) {
        if (value != null && (!Double.isFinite(value) || value < 0)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, field + " must be finite and non-negative");
        }
    }

    private void attachReferences(InventoryItem inventoryItem) {
        Integer categoryId = inventoryItem.getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Category with ID " + categoryId + " was not found"
                ));

        Integer unitTypeId = inventoryItem.getUnitTypeId();
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
