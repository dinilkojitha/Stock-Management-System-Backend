package com.example.stockmanagementsystembackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "stock_has_inventoryitem")
public class StockHasInventoryitem {
    @EmbeddedId
    private StockHasInventoryitemId id;

    @MapsId("stockBatchid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Stock_batchID", nullable = false)
    private Stock stockBatchid;

    @MapsId("inventoryitemItemid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "InventoryItem_itemId", nullable = false)
    private Inventoryitem inventoryitemItem;

}