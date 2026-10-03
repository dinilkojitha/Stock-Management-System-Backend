package com.example.stockmanagementsystembackend.domain.procurement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class DeliveryUpdateRequest {
    @NotBlank
    private String status;

    private LocalDate actualDeliveryDate;
}