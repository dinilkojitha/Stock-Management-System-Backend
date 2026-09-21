package com.example.stockmanagementsystembackend.domain.organization.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DepartmentResponse {
    private Integer id;
    private String departmentName;
    private String location;
    private Integer branchId;
    private String branchName;
}