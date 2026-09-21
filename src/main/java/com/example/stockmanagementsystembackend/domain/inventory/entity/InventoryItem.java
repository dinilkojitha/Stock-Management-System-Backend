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
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "InventoryItem")
public class InventoryItem {
    // Encapsulation protects inventory data behind getters and setters.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id", nullable = false)
    private Integer id;

    @Size(max = 60)
    @Column(name = "name", length = 60)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "total_quantity")
    private Double totalQuantity;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "unit_type_id", nullable = false)
    private UnitType unitType;

    @Column(name = "unit_price")
    private Double unitPrice;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "reorder_threshold")
    private Double reorderThreshold;

    public InventoryItem() {
    }

    public InventoryItem(Integer id, String name, Category category, Double totalQuantity,
                         UnitType unitType, Double unitPrice, String description,
                         Double reorderThreshold) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.totalQuantity = totalQuantity;
        this.unitType = unitType;
        this.unitPrice = unitPrice;
        this.description = description;
        this.reorderThreshold = reorderThreshold;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Preserves compatibility with existing stock-module code while the JSON and
     * database property remains {@code name}.
     */
    @JsonIgnore
    public String getItemName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getTotalQuantity() {
        return totalQuantity;
    }

    public void setTotalQuantity(Double totalQuantity) {
        this.totalQuantity = totalQuantity;
    }

    public UnitType getUnitType() {
        return unitType;
    }

    public void setUnitType(UnitType unitType) {
        this.unitType = unitType;
    }

    public Double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(Double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getReorderThreshold() {
        return reorderThreshold;
    }

    public void setReorderThreshold(Double reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }
}
