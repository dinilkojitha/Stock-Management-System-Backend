package com.example.stockmanagementsystembackend.domain.procurement.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EvaluationRequest {
    @NotNull
    private Integer evaluatedByUserId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    @Min(1)
    @Max(5)
    private Integer deliveryRating;

    @Min(1)
    @Max(5)
    private Integer qualityRating;

    private String comments;
}