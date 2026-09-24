package com.example.stockmanagementsystembackend.domain.distribution.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class ConsumptionResponseDTO {

    private Integer consumptionId;
    private Integer requestId;
    private Integer departmentId;
    private Integer itemId;
    private Double allocatedQuantity;
    private Double quantityConsumed;
    private Instant consumedAt;
}