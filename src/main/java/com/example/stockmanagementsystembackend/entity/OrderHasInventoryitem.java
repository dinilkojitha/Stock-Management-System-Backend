package com.example.stockmanagementsystembackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "order_has_inventoryitem")
public class OrderHasInventoryitem {
    @EmbeddedId
    private OrderHasInventoryitemId id;

    @MapsId("orderOrderid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Order_orderID", nullable = false)
    private Order orderOrderid;

    @MapsId("inventoryitemItemid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "InventoryItem_itemId", nullable = false)
    private Inventoryitem inventoryitemItem;

    @Size(max = 45)
    @Column(name = "quantity", length = 45)
    private String quantity;

}