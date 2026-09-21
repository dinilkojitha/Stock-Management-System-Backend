package com.example.stockmanagementsystembackend.domain.organization.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BranchSummaryResponse {
    private Integer id;
    private String branchName;
    private String location;
    private Integer totalStockBatches;
    private Integer departmentCount;
}