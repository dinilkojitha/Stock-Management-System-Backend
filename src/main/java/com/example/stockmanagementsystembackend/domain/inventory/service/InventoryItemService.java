package com.example.stockmanagementsystembackend.domain.inventory.service;

import com.example.stockmanagementsystembackend.domain.inventory.Dto.ItermDto;
import com.example.stockmanagementsystembackend.domain.inventory.entity.Category;
import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.entity.UnitType;
import com.example.stockmanagementsystembackend.domain.inventory.repository.CategoryRepository;
import com.example.stockmanagementsystembackend.domain.inventory.repository.InventoryItemRepository;
import com.example.stockmanagementsystembackend.domain.inventory.repository.UnitTypeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
// Polymorphism allows this implementation to be referenced by the shared CRUD type.
public class InventoryItemService {
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


    public InventoryItem create(InventoryItem inventoryItem) {
        return save(inventoryItem);
    }


    public InventoryItem save(InventoryItem inventoryItem) {
        validateInventoryItem(inventoryItem);
        inventoryItem.setId(null);
        inventoryItem.setArchived(false);
        attachReferences(inventoryItem);
        return inventoryItemRepository.save(inventoryItem);
    }


    public List<InventoryItem> getAll() {
        return inventoryItemRepository.findByArchivedFalseOrderByItemNameAsc();
    }

    public List<ItermDto> getAllWraped() {
        List<InventoryItem> inventoryItems = inventoryItemRepository.findByArchivedFalseOrderByItemNameAsc();
        return inventoryItems.stream().map(inventoryItem -> {
            ItermDto dto = new ItermDto();
            dto.setId(inventoryItem.getId());
            dto.setItemName(inventoryItem.getItemName());
            return dto;
        }).collect(java.util.stream.Collectors.toList());
    }

    public List<InventoryItem> getArchived() {
        return inventoryItemRepository.findByArchivedTrueOrderByItemNameAsc();
    }

    public InventoryItem getById(Integer id) {
        return inventoryItemRepository.findById(id)
                .orElseThrow(() -> inventoryItemNotFound(id));
    }


    @Transactional
    public InventoryItem update(Integer id, InventoryItem inventoryItem) {
        validateInventoryItem(inventoryItem);

        InventoryItem existingItem = inventoryItemRepository.findByIdForUpdate(id)
                .orElseThrow(() -> inventoryItemNotFound(id));
        attachReferences(inventoryItem);
        existingItem.setItemName(inventoryItem.getItemName());
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
        return inventoryItemRepository.findByArchivedFalseAndItemNameContainingIgnoreCaseOrderByItemNameAsc(
                keyword == null ? "" : keyword.strip());
    }

    @Transactional(readOnly = true)
    public List<InventoryItem> getByCategory(Integer categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Category was not found");
        }
        return inventoryItemRepository.findByArchivedFalseAndCategory_CategoryIdOrderByItemNameAsc(categoryId);
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


    @Transactional
    public String archiveInventoryItem(Integer id) {
        InventoryItem inventoryItem = inventoryItemRepository.findById(id)
                .orElseThrow(() -> inventoryItemNotFound(id));
        inventoryItem.setArchived(true);
        inventoryItemRepository.save(inventoryItem);
        return "Inventory item archived successfully";
    }

    @Transactional
    public String restoreInventoryItem(Integer id) {
        InventoryItem inventoryItem = inventoryItemRepository.findById(id)
                .orElseThrow(() -> inventoryItemNotFound(id));
        inventoryItem.setArchived(false);
        inventoryItemRepository.save(inventoryItem);
        return "Inventory item restored successfully";
    }

    private void validateInventoryItem(InventoryItem inventoryItem) {
        if (inventoryItem == null || inventoryItem.getItemName() == null || inventoryItem.getItemName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name must not be blank");
        }
        inventoryItem.setItemName(inventoryItem.getItemName());
        if (inventoryItem.getItemName().length() > 60) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name must not exceed 60 characters");
        }
        if (inventoryItem.getCategory() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "categoryId is required");
        }
        if (inventoryItem.getUnitType() == null) {
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
        Integer categoryId = inventoryItem.getCategory().getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Category with ID " + categoryId + " was not found"
                ));

        Integer unitTypeId = inventoryItem.getUnitType().getUnitTypeId();
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
