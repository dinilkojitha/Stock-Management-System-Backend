package com.example.stockmanagementsystembackend.domain.stock.controller;

import com.example.stockmanagementsystembackend.domain.stock.entity.Stock;
import com.example.stockmanagementsystembackend.domain.stock.dto.request.StockIssueRequest;
import com.example.stockmanagementsystembackend.domain.stock.dto.request.StockReceiptRequest;
import com.example.stockmanagementsystembackend.domain.stock.dto.response.StockMovementResponse;
import com.example.stockmanagementsystembackend.domain.stock.service.StockMovementService;
import com.example.stockmanagementsystembackend.domain.stock.service.StockService;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URI;
import java.util.List;
import jakarta.validation.Valid;

@CrossOrigin
@RestController
@RequestMapping("/api/stocks")
public class StockController {
    private final StockService service;
    private final StockMovementService movementService;

    @Autowired
    public StockController(StockService service, StockMovementService movementService) {
        this.service = service;
        this.movementService = movementService;
    }

    public StockController(StockService service) {
        this.service = service;
        this.movementService = null;
    }

    @GetMapping
    public ResponseEntity<List<Stock>> getAll() { return ResponseEntity.ok(service.getAll()); }

    @GetMapping("/{stockId}")
    public ResponseEntity<Stock> getById(@PathVariable Integer stockId) {
        return ResponseEntity.ok(service.getById(stockId));
    }

    @PostMapping
    public ResponseEntity<Stock> create(@Valid @RequestBody Stock request) {
        Stock stock = service.save(request);
        return ResponseEntity.created(URI.create("/api/stocks/" + stock.getStockId())).body(stock);
    }

    @PostMapping("/receive")
    public ResponseEntity<StockMovementResponse> receive(@Valid @RequestBody StockReceiptRequest request) {
        return ResponseEntity.status(201).body(movementService.receive(request));
    }

    @PostMapping("/issue")
    public ResponseEntity<StockMovementResponse> issue(@Valid @RequestBody StockIssueRequest request) {
        return ResponseEntity.ok(movementService.issue(request));
    }

    @PutMapping("/{stockId}")
    public ResponseEntity<Stock> update(@PathVariable Integer stockId, @Valid @RequestBody Stock request) {
        return ResponseEntity.ok(service.update(stockId, request));
    }

    @DeleteMapping("/{stockId}")
    public ResponseEntity<Void> delete(@PathVariable Integer stockId) {
        service.delete(stockId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/branch/{branchId}")
    public ResponseEntity<List<Stock>> getByBranch(@PathVariable Integer branchId) {
        return ResponseEntity.ok(service.getByBranch(branchId));
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<List<Stock>> getByItem(@PathVariable Integer itemId) {
        return ResponseEntity.ok(service.getByItem(itemId));
    }

    @GetMapping("/expiring")
    public ResponseEntity<List<Stock>> expiring(@RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(service.getExpiring(days));
    }

    @GetMapping("/expired")
    public ResponseEntity<List<Stock>> expired() { return ResponseEntity.ok(service.getExpired()); }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ProblemDetail> handleStockError(ResponseStatusException exception) {
        return ResponseEntity.status(exception.getStatusCode()).body(
                ProblemDetail.forStatusAndDetail(exception.getStatusCode(), exception.getReason()));
    }


}
