package com.example.stockmanagementsystembackend.domain.procurement.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemRequest {
    @NotNull
    private Integer itemId;

    @NotNull
    private Double quantity;
}