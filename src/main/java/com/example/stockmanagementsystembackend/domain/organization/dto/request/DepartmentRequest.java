package com.example.stockmanagementsystembackend.domain.organization.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentRequest {
    @NotBlank
    @Size(max = 45)
    private String departmentName;
    @NotNull
    private Integer branchId;
    @Size(max = 100)
    private String location;
}