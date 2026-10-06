package com.example.stockmanagementsystembackend.domain.stock.entity;

import com.example.stockmanagementsystembackend.domain.organization.entity.Branch;
import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
@Setter
@Getter
@Entity
@Table(name = "Stock")
public class Stock  {
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

    @JsonIgnore
    public boolean isNew() { return newStock; }

    @PostLoad
    @PostPersist
    private void markPersisted() { newStock = false; }




}
