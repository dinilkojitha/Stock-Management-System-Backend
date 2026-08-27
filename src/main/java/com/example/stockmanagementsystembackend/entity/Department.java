package com.example.stockmanagementsystembackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "department")
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "departmentId", nullable = false)
    private Integer id;

    @Size(max = 45)
    @Column(name = "departmentName", length = 45)
    private String departmentName;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Branch_branchID", nullable = false)
    private Branch branchBranchid;

    @Size(max = 100)
    @Column(name = "location", length = 100)
    private String location;

}