package com.example.stockmanagementsystembackend.domain.stock.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferItemDto {
    @NotNull
    private Integer inventoryItemId;
    @NotNull
    @Positive
    private Double quantity;
}