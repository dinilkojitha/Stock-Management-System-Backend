package com.example.stockmanagementsystembackend.domain.distribution.controller;

import com.example.stockmanagementsystembackend.domain.distribution.dto.StockAllocationRequestDTO;
import com.example.stockmanagementsystembackend.domain.distribution.service.StockAllocationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internal-requests")
public class StockAllocationController {

    private final StockAllocationService stockAllocationService;

    public StockAllocationController(
            StockAllocationService stockAllocationService) {

        this.stockAllocationService = stockAllocationService;
    }

    @PostMapping("/{requestId}/allocate")
    public void allocateStock(
            @PathVariable("requestId") Integer requestId,
            @RequestBody StockAllocationRequestDTO allocationRequest) {

        stockAllocationService.allocateStock(
                requestId,
                allocationRequest
        );
    }
}