package com.example.stockmanagementsystembackend.domain.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "unittype")
public class UnitType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unitID", nullable = false)
    private Integer id;

    @Size(max = 45)
    @Column(name = "unit", length = 45)
    private String unit;

}
