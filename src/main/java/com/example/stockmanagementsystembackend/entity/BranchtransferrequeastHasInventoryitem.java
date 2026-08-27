package com.example.stockmanagementsystembackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "branchtransferrequeast_has_inventoryitem")
public class BranchtransferrequeastHasInventoryitem {
    @EmbeddedId
    private BranchtransferrequeastHasInventoryitemId id;

    @MapsId("branchtransferrequeastOrderid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BranchTransferRequeast_OrderID", nullable = false, referencedColumnName = "TransferRequeastID")
    private Branchtransferrequest branchtransferrequeastOrderid;

    @MapsId("inventoryitemItemid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "InventoryItem_itemId", nullable = false)
    private Inventoryitem inventoryitemItem;

    @Column(name = "quantity")
    private Double quantity;

}