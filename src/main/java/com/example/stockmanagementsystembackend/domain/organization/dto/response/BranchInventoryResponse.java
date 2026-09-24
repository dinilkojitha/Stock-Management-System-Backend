package com.example.stockmanagementsystembackend.domain.organization.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class BranchInventoryResponse {
    private Integer stockId;
    private Integer branchId;
    private String branchName;
    private Double quantity;
    private LocalDate manufactureDate;
    private LocalDate expiryDate;
}