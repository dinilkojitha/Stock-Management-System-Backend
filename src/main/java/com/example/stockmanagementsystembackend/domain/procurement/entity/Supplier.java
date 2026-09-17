package com.example.stockmanagementsystembackend.domain.procurement.entity;

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
    @Column(name = "supplier_id", nullable = false)
    private Integer id;

    @Size(max = 50)
    @Column(name = "company_name", length = 50)
    private String companyName;

    @Size(max = 50)
    @Column(name = "contact_person", length = 50)
    private String contactPerson;

    @Size(max = 60)
    @Column(name = "email", length = 60)
    private String email;

    @Size(max = 45)
    @Column(name = "phone_number", length = 45)
    private String phoneNumber;

    @Size(max = 100)
    @Column(name = "address", length = 100)
    private String address;

}