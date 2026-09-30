package com.example.stockmanagementsystembackend.domain.procurement.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SupplierRequest {
    @NotBlank
    @Size(max = 50)
    private String companyName;

    @Size(max = 50)
    private String contactPerson;

    @Email
    @Size(max = 60)
    private String email;

    @Size(max = 45)
    private String phoneNumber;

    @Size(max = 100)
    private String address;
}