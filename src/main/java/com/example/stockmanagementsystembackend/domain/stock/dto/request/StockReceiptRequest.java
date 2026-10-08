package com.example.stockmanagementsystembackend.domain.stock.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record StockReceiptRequest(
        @NotNull @Positive Integer stockId,
        @NotNull @Positive Integer itemId,
        @NotNull @Positive Integer branchId,
        @NotNull @Positive Double quantity,
        @PositiveOrZero Integer userId,
        LocalDate manufactureDate,
        LocalDate expiryDate,
        @Size(max = 120) String remarks
) {
}
