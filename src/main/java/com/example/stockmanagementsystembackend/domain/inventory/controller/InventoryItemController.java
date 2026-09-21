package com.example.stockmanagementsystembackend.domain.inventory.controller;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.service.InventoryItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        InventoryItem createdItem = inventoryItemService.create(inventoryItem);
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
