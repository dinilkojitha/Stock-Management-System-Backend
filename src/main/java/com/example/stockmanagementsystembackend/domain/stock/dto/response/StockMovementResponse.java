package com.example.stockmanagementsystembackend.domain.stock.dto.response;

import java.util.List;

public record StockMovementResponse(
        Integer itemId,
        String itemName,
        Integer branchId,
        Double quantityMoved,
        Double remainingItemQuantity,
        List<Integer> affectedStockIds
) {
}
