package com.example.stockmanagementsystembackend.domain.procurement.entity;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "purchase_order_items")
public class PurchaseOrderItem {
    @EmbeddedId
    private PurchaseOrderItemId id;

    @MapsId("purchaseOrderId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private Order purchaseOrder;

    @MapsId("itemId")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "unit_cost")
    private Double unitCost;

}