package com.example.stockmanagementsystembackend.domain.inventory.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "UnitType")
public class UnitType {
    // Encapsulation keeps the entity state controlled by its public API.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_type_id", nullable = false)
    private Integer unitTypeId;

    @NotBlank(message = "Unit type name must not be blank")
    @Size(max = 45, message = "Unit type name must not exceed 45 characters")
    @Column(name = "name", length = 45)
    private String name;


}
