package com.example.stockmanagementsystembackend.domain.organization.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class BranchOverviewResponse {
    private List<BranchSummaryResponse> branches;
    private Integer totalBranches;
}