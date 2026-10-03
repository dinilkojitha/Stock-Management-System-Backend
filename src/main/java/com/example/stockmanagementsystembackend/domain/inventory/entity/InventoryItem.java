package com.example.stockmanagementsystembackend.domain.inventory.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "inventoryitem")
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "itemId", nullable = false)
    private Integer id;

    @Size(max = 60)
    @Column(name = "itemName", length = 60)
    private String itemName;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Category_categoryID", nullable = false)
    private Category categoryCategoryid;                                        //

    @Column(name = "totalQuantity")
    private Double totalQuantity;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "UnitType_unitID", nullable = false)
    private UnitType unittypeUnitid;

    @Column(name = "unitPrice")
    private Double unitPrice;

    @Lob
    @Column(name = "itemDescription")
    private String itemDescription;

    @Column(name = "reorderThreshold")
    private Double reorderThreshold;

}