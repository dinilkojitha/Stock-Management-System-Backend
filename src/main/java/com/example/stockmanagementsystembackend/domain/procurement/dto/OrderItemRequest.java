package com.example.stockmanagementsystembackend.domain.procurement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequest {
    @NotNull
    private Integer itemId;

    @NotNull
    @Positive
    private Double quantity;

    @NotNull
    @Positive
    private Double unitCost;
}