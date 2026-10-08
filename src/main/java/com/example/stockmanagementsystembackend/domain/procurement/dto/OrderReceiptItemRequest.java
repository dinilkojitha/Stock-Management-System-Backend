package com.example.stockmanagementsystembackend.domain.procurement.dto;

import java.time.LocalDate;

public record OrderReceiptItemRequest(
        Integer itemId,
        Double quantity,
        Integer stockId,
        LocalDate manufactureDate,
        LocalDate expiryDate
) {
}
