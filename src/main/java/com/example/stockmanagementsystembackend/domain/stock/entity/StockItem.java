package com.example.stockmanagementsystembackend.domain.stock.entity;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "stock_has_inventoryitem")
public class StockItem {
    @EmbeddedId
    private StockItemId id;

    @MapsId("stockBatchid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Stock_batchID", nullable = false)
    private Stock stockBatchid;

    @MapsId("inventoryitemItemid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "InventoryItem_itemId", nullable = false)
    private InventoryItem inventoryitemItem;

}
