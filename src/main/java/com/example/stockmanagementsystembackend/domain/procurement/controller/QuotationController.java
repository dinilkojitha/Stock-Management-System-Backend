package com.example.stockmanagementsystembackend.domain.procurement.controller;

import com.example.stockmanagementsystembackend.domain.procurement.dto.QuotationRequest;
import com.example.stockmanagementsystembackend.domain.procurement.service.QuotationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/quotations")
public class QuotationController {
    private final QuotationService quotationService;

    public QuotationController(QuotationService quotationService) {
        this.quotationService = quotationService;
    }

    @PostMapping
    public Object create(@Valid @RequestBody QuotationRequest request) {
        return quotationService.create(request);
    }

    @GetMapping
    public Object findAll(@RequestParam(required = false) Integer itemId) {
        return quotationService.findAll(itemId);
    }
}