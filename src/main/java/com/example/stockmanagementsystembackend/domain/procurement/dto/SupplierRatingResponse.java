package com.example.stockmanagementsystembackend.domain.procurement.dto;

public record SupplierRatingResponse(Integer supplierId, String supplierName, Double averageRating,
                                     Long evaluationCount) {
}