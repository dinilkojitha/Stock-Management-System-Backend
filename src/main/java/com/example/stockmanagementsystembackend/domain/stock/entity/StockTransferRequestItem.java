package com.example.stockmanagementsystembackend.domain.stock.entity;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "branchtransferrequeast_has_inventoryitem")
public class StockTransferRequestItem {
    @EmbeddedId
    private StockTransferRequestItemId id;

    @MapsId("branchtransferrequeastOrderid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BranchTransferRequeast_OrderID", nullable = false, referencedColumnName = "TransferRequeastID")
    private StockTransferRequest branchtransferrequeastOrderid;

    @MapsId("inventoryitemItemid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "InventoryItem_itemId", nullable = false)
    private InventoryItem inventoryitemItem;

    @Column(name = "quantity")
    private Double quantity;

}
