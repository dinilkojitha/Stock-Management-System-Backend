package com.example.stockmanagementsystembackend.domain.stock.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransferItemResponseDto {
    private Integer inventoryItemId;
    private String inventoryItemName;
    private Double quantity;
}