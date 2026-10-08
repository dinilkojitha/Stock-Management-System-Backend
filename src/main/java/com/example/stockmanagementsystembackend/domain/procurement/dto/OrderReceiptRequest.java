package com.example.stockmanagementsystembackend.domain.procurement.dto;

import java.time.LocalDate;
import java.util.List;

public record OrderReceiptRequest(
        Integer branchId,
        Integer receivedByUserId,
        LocalDate actualDeliveryDate,
        List<OrderReceiptItemRequest> items
) {
}
