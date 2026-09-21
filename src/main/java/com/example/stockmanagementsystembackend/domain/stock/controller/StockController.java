package com.example.stockmanagementsystembackend.domain.stock.controller;

import com.example.stockmanagementsystembackend.domain.stock.entity.Stock;
import com.example.stockmanagementsystembackend.domain.stock.service.StockService;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/stocks")
public class StockController {
    private final StockService service;

    public StockController(StockService service) { this.service = service; }

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
