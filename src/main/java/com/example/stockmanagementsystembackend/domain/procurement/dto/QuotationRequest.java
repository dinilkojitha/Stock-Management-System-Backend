package com.example.stockmanagementsystembackend.domain.procurement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class QuotationRequest {
    @NotNull
    private Integer supplierId;

    @NotNull
    private Integer itemId;

    @NotNull
    @Positive
    private Double quotedUnitCost;

    @Positive
    private Double availableQuantity;

    private LocalDate validUntil;
    private String status;
    private String notes;
}