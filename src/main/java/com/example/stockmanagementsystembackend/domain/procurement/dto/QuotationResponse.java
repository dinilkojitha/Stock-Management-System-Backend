package com.example.stockmanagementsystembackend.domain.procurement.dto;

import java.time.Instant;
import java.time.LocalDate;

public record QuotationResponse(Integer id, Integer supplierId, String supplierName, Integer itemId,
                                String itemName, Double quotedUnitCost, Double availableQuantity,
                                LocalDate validUntil, String status, String notes, Instant createdAt) {
}