package com.example.stockmanagementsystembackend.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Size;

public class BranchRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "branchID", nullable = false)
    private Integer id;

    @Size(max = 45)
    @Column(name = "branchName", length = 45)
    private String branchName;

    @Size(max = 45)
    @Column(name = "location", length = 45)
    private String location;
}
