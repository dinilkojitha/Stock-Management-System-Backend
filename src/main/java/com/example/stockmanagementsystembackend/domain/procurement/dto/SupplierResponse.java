package com.example.stockmanagementsystembackend.domain.procurement.dto;

public record SupplierResponse(
        Integer id,
        String companyName,
        String contactPerson,
        String email,
        String phoneNumber,
        String address) {
}