package com.example.stockmanagementsystembackend.domain.procurement.dto;

public record OrderItemResponse(Integer itemId,
                                String itemName,
                                Double quantity,
                                Double unitCost) {
}