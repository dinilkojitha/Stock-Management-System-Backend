package com.example.stockmanagementsystembackend.domain.distribution.controller;

import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequestItem;
import com.example.stockmanagementsystembackend.domain.distribution.service.InternalRequestItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/internal-request-items")
@CrossOrigin
public class InternalRequestItemController {

    private final InternalRequestItemService internalRequestItemService;

    public InternalRequestItemController(InternalRequestItemService internalRequestItemService) {
        this.internalRequestItemService = internalRequestItemService;
    }

    @GetMapping
    public ResponseEntity<List<InternalRequestItem>> getAllItems() {
        return ResponseEntity.ok(internalRequestItemService.getAllItems());
    }

    @PostMapping
    public ResponseEntity<InternalRequestItem> createItem(
            @RequestBody InternalRequestItem item) {

        return ResponseEntity.ok(
                internalRequestItemService.createItem(item)
        );
    }

    @PutMapping("/{requestId}/{itemId}")
    public ResponseEntity<InternalRequestItem> updateItem(
            @PathVariable("requestId") Integer requestId,
            @PathVariable("itemId") Integer itemId,
            @RequestBody InternalRequestItem item) {

        return ResponseEntity.ok(
                internalRequestItemService.updateItem(requestId, itemId, item)
        );
    }

    @DeleteMapping("/{requestId}/{itemId}")
    public ResponseEntity<Void> deleteItem(
            @PathVariable("requestId") Integer requestId,
            @PathVariable("itemId") Integer itemId) {

        internalRequestItemService.deleteItem(requestId, itemId);

        return ResponseEntity.noContent().build();
    }
}