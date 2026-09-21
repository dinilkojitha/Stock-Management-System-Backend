package com.example.stockmanagementsystembackend.domain.inventory.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    private Category categoryCategoryid;

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
