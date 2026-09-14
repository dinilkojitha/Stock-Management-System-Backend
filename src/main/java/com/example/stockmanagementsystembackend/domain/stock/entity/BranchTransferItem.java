package com.example.stockmanagementsystembackend.domain.stock.entity;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "branch_transfer_items")
public class BranchTransferItem {
    @EmbeddedId
    private BranchTransferItemId id;

    @MapsId("itemId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    @Column(name = "quantity")
    private Double quantity;

}