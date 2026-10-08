package com.example.stockmanagementsystembackend.domain.stock.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record StockIssueRequest(
        @NotNull @Positive Integer itemId,
        @NotNull @Positive Integer branchId,
        @NotNull @Positive Double quantity,
        @NotNull @Positive Integer userId,
        @Size(max = 120) String remarks
) {
}
