package com.example.stockmanagementsystembackend.domain.distribution.controller;

import com.example.stockmanagementsystembackend.domain.distribution.dto.ConsumptionDTO;
import com.example.stockmanagementsystembackend.domain.distribution.entity.Consumption;
import com.example.stockmanagementsystembackend.domain.distribution.service.ConsumptionService;
import org.springframework.web.bind.annotation.*;
import com.example.stockmanagementsystembackend.domain.distribution.dto.ConsumptionResponseDTO;

import java.util.List;

@RestController
@RequestMapping("/api/consumptions")
@CrossOrigin
public class ConsumptionController {

    private final ConsumptionService consumptionService;

    public ConsumptionController(ConsumptionService consumptionService) {
        this.consumptionService = consumptionService;
    }

    @PostMapping
    public List<ConsumptionResponseDTO> createConsumption(
            @RequestBody ConsumptionDTO dto) {

        return consumptionService.createConsumption(dto);
    }
    @GetMapping
    public List<ConsumptionResponseDTO> getAllConsumptions() {

        return consumptionService.getAllConsumptions();
    }

    @PutMapping("/{id}")
    public Consumption updateConsumption(
            @PathVariable("id") Integer id,
            @RequestBody Double quantityConsumed) {

        return consumptionService.updateConsumption(id, quantityConsumed);
    }
}