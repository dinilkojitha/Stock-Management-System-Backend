package com.example.stockmanagementsystembackend.domain.distribution.controller;

import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequestItem;
import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequestItemId;
import com.example.stockmanagementsystembackend.domain.distribution.service.InternalRequestItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/internal-request-items")
public class InternalRequestItemController {

    private final InternalRequestItemService internalRequestItemService;

    public InternalRequestItemController(
            InternalRequestItemService internalRequestItemService) {
        this.internalRequestItemService = internalRequestItemService;
    }

    @GetMapping
    public ResponseEntity<?> getAllRequestItems() {
        return ResponseEntity.ok(
                internalRequestItemService.getAllRequestItems()
        );
    }

    @PostMapping
    public ResponseEntity<?> createRequestItem(
            @RequestBody InternalRequestItem requestItem) {

        return ResponseEntity.ok(
                internalRequestItemService.createRequestItem(requestItem)
        );
    }

    @PutMapping("/{requestId}/{itemId}")
    public ResponseEntity<?> updateRequestItem(
            @PathVariable("requestId") Integer requestId,
            @PathVariable("itemId") Integer itemId,
            @RequestBody InternalRequestItem requestItem) {

        InternalRequestItemId id = new InternalRequestItemId();
        id.setInternalrequestOrdertid(requestId);
        id.setInventoryitemItemid(itemId);

        return ResponseEntity.ok(
                internalRequestItemService.updateRequestItem(id, requestItem)
        );
    }

    @DeleteMapping("/{requestId}/{itemId}")
    public ResponseEntity<?> deleteRequestItem(
            @PathVariable("requestId") Integer requestId,
            @PathVariable("itemId") Integer itemId) {

        InternalRequestItemId id = new InternalRequestItemId();
        id.setInternalrequestOrdertid(requestId);
        id.setInventoryitemItemid(itemId);

        internalRequestItemService.deleteRequestItem(id);

        return ResponseEntity.ok().build();
    }
}