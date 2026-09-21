package com.example.stockmanagementsystembackend.domain.organization.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchRequest {
    @NotBlank
    @Size(max = 45)
    private String branchName;

    @Size(max = 45)
    private String location;
}
