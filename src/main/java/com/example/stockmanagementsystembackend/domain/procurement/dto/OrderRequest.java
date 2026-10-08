package com.example.stockmanagementsystembackend.domain.procurement.dto;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private Integer supplierId;
    private Integer branchId;
    private Integer createdByUserId;
    private LocalDate expectedDeliveryDate;
    private List<OrderItemRequest> items;
}