package com.example.stockmanagementsystembackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "`order`")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderID", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "User_userID", nullable = false)
    private User userUserid;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Supplier_supplierID", nullable = false)
    private Supplier supplierSupplierid;

    @Column(name = "orderDate")
    private Instant orderDate;

    @Column(name = "expectedDeliveryDate")
    private LocalDate expectedDeliveryDate;

    @Column(name = "actualDeliveryDate")
    private LocalDate actualDeliveryDate;

    @Column(name = "totalCost")
    private Double totalCost;

    @Size(max = 45)
    @Column(name = "status", length = 45)
    private String status;

}