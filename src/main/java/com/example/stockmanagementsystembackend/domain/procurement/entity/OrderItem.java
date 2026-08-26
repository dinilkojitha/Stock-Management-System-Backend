package com.example.stockmanagementsystembackend.domain.procurement.entity;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_has_inventoryitem")
public class OrderItem {
    @EmbeddedId
    private OrderItemId id;

    @MapsId("orderOrderid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Order_orderID", nullable = false)
    private Order orderOrderid;

    @MapsId("inventoryitemItemid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "InventoryItem_itemId", nullable = false)
    private InventoryItem inventoryitemItem;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "purchasePrice")
    private Double purchasePrice;

}
