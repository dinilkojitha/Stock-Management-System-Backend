package com.example.stockmanagementsystembackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "supplier")
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplierID", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "companyName", length = 50)
    private String companyName;

    @Size(max = 50)
    @Column(name = "contactPerson", length = 50)
    private String contactPerson;

    @Size(max = 60)
    @Column(name = "email", length = 60)
    private String email;

    @Size(max = 45)
    @Column(name = "phone", length = 45)
    private String phone;

    @Size(max = 100)
    @Column(name = "address", length = 100)
    private String address;

}