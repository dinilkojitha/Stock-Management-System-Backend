package com.example.stockmanagementsystembackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "branch")
public class Branch {
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