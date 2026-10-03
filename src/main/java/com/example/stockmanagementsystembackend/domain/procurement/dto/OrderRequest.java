package com.example.stockmanagementsystembackend.domain.procurement.dto;

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
    @NotNull
    private Integer supplierId;

    @NotNull
    private Integer createdByUserId;

    private LocalDate expectedDeliveryDate;

    @NotEmpty
    @Valid
    private List<OrderItemRequest> items;
}