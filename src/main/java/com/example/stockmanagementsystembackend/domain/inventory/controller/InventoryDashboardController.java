package com.example.stockmanagementsystembackend.domain.inventory.controller;

import com.example.stockmanagementsystembackend.domain.inventory.service.InventoryDashboardService;
import com.example.stockmanagementsystembackend.domain.inventory.service.InventoryDashboardService.InventoryDashboardSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/api/inventory/dashboard")
public class InventoryDashboardController {
    private final InventoryDashboardService dashboardService;

    public InventoryDashboardController(InventoryDashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<InventoryDashboardSummary> getDashboard() {
        return ResponseEntity.ok(dashboardService.getSummary());
    }
}
