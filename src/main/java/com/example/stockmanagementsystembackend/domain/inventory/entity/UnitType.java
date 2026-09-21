package com.example.stockmanagementsystembackend.domain.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

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

    public UnitType() {
    }

    public UnitType(String name) {
        this.name = name;
    }

    public UnitType(Integer unitTypeId, String name) {
        this.unitTypeId = unitTypeId;
        this.name = name;
    }

    public Integer getUnitTypeId() {
        return unitTypeId;
    }

    public void setUnitTypeId(Integer unitTypeId) {
        this.unitTypeId = unitTypeId;
    }

    /**
     * Compatibility accessor for existing inventory relationships that use a
     * generic entity ID.
     */
    @JsonIgnore
    public Integer getId() {
        return unitTypeId;
    }

    @JsonIgnore
    public void setId(Integer id) {
        this.unitTypeId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
