package com.example.stockmanagementsystembackend.domain.procurement.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public record OrderResponse(
        Integer id,
        Integer supplierId,
        String supplierName,
        Integer createdByUserId,
        Instant orderDate,
        LocalDate expectedDeliveryDate,
        LocalDate actualDeliveryDate,
        Double totalCost,
        String status,
        List<OrderItemResponse> items) {
}