package com.example.stockmanagementsystembackend.domain.stock.entity;

import com.example.stockmanagementsystembackend.domain.organization.entity.Branch;
import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.domain.Persistable;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "Stock")
public class Stock implements Persistable<Integer> {
    // Assigned by the client: the existing stock_id column is NOT auto-increment.
    @Id
    @Column(name = "stock_id", nullable = false)
    private Integer stockId;

    @NotNull(message = "quantity is required")
    @PositiveOrZero(message = "quantity must not be negative")
    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    // No cascade: batch CRUD must never create/delete InventoryItem records.
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "stock_items",
            joinColumns = @JoinColumn(name = "stock_id"),
            inverseJoinColumns = @JoinColumn(name = "item_id"))
    private Set<InventoryItem> items = new LinkedHashSet<>();

    @Transient
    private boolean newStock = true;

    public Stock() {
    }

    public Stock(Integer stockId, Double quantity, LocalDate manufactureDate,
                 LocalDate expiryDate, Branch branch) {
        this.stockId = stockId;
        this.quantity = quantity;
        this.manufactureDate = manufactureDate;
        this.expiryDate = expiryDate;
        this.branch = branch;
    }

    public Integer getStockId() { return stockId; }
    public void setStockId(Integer stockId) { this.stockId = stockId; }

    @Override
    @JsonIgnore
    public Integer getId() { return stockId; }
    public void setId(Integer id) { this.stockId = id; }

    // Persist assigned IDs as INSERTs; merge must not overwrite an existing batch.
    @Override
    @JsonIgnore
    public boolean isNew() { return newStock; }

    @PostLoad
    @PostPersist
    private void markPersisted() { newStock = false; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }
    public LocalDate getManufactureDate() { return manufactureDate; }
    public void setManufactureDate(LocalDate manufactureDate) { this.manufactureDate = manufactureDate; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    @JsonIgnore
    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }

    @Transient
    @NotNull(message = "branchId is required")
    public Integer getBranchId() { return branch == null ? null : branch.getId(); }

    public void setBranchId(Integer branchId) {
        if (branchId == null) {
            branch = null;
        } else {
            branch = new Branch();
            branch.setId(branchId);
        }
    }

    @JsonIgnore
    public Set<InventoryItem> getItems() { return items; }
    public void setItems(Set<InventoryItem> items) { this.items = items; }

    @Transient
    public Set<Integer> getItemIds() {
        return items == null ? null : items.stream().map(InventoryItem::getId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void setItemIds(Set<Integer> itemIds) {
        items = itemIds == null ? null : itemIds.stream().map(id -> {
            InventoryItem item = new InventoryItem();
            item.setId(id);
            return item;
        }).collect(Collectors.toCollection(LinkedHashSet::new));
    }
}
