package com.example.stockmanagementsystembackend.domain.organization.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BranchPerformanceResponse {
    private Integer branchId;
    private String branchName;
    private Integer stockBatchCount;
    private Double totalStockQuantity;
    private Integer departmentCount;
    private Long incomingTransferCount;
    private Long outgoingTransferCount;
    private Long pendingIncomingTransferCount;
    private Long pendingOutgoingTransferCount;
}