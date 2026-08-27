package com.example.stockmanagementsystembackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "stock")
public class Stock {
    @Id
    @Column(name = "stockID", nullable = false)
    private Integer id;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "manufactureDate")
    private LocalDate manufactureDate;

    @Column(name = "expiryDate")
    private LocalDate expiryDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Branch_branchID", nullable = false)
    private Branch branchBranchid;

}