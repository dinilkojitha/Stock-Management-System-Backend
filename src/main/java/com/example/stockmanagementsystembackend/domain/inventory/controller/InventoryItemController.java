package com.example.stockmanagementsystembackend.domain.inventory.controller;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.service.InventoryItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/inventory-items")
public class InventoryItemController {
    private final InventoryItemService inventoryItemService;

    public InventoryItemController(InventoryItemService inventoryItemService) {
        this.inventoryItemService = inventoryItemService;
    }

    @PostMapping
    public ResponseEntity<InventoryItem> createInventoryItem(
            @Valid @RequestBody InventoryItem inventoryItem
    ) {
        InventoryItem createdItem = inventoryItemService.save(inventoryItem);
        URI location = URI.create("/api/inventory-items/" + createdItem.getId());
        return ResponseEntity.created(location).body(createdItem);
    }

    @GetMapping
    public ResponseEntity<List<InventoryItem>> getAllInventoryItems() {
        return ResponseEntity.ok(inventoryItemService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> getInventoryItemById(@PathVariable Integer id) {
        return ResponseEntity.ok(inventoryItemService.getById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<InventoryItem>> search(@RequestParam(defaultValue = "") String keyword) {
        return ResponseEntity.ok(inventoryItemService.search(keyword));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<InventoryItem>> getByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(inventoryItemService.getByCategory(categoryId));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryItem>> getLowStock() {
        return ResponseEntity.ok(inventoryItemService.getLowStock());
    }

    @PutMapping("/{id}/quantity")
    public ResponseEntity<InventoryItem> adjustQuantity(
            @PathVariable Integer id, @Valid @RequestBody QuantityAdjustment request) {
        return ResponseEntity.ok(inventoryItemService.adjustQuantity(id, request.getQuantityDelta()));
    }

    // This handler is local to InventoryItem; other modules retain their error behavior.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleInventoryError(ResponseStatusException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(exception.getStatusCode(), exception.getReason());
        return ResponseEntity.status(exception.getStatusCode()).body(problem);
    }

    public static class QuantityAdjustment {
        @NotNull(message = "quantityDelta is required")
        private Double quantityDelta;

        public QuantityAdjustment() {
        }

        public Double getQuantityDelta() {
            return quantityDelta;
        }

        public void setQuantityDelta(Double quantityDelta) {
            this.quantityDelta = quantityDelta;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItem> updateInventoryItem(
            @PathVariable Integer id,
            @Valid @RequestBody InventoryItem inventoryItem
    ) {
        return ResponseEntity.ok(inventoryItemService.update(id, inventoryItem));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInventoryItem(@PathVariable Integer id) {
        inventoryItemService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
