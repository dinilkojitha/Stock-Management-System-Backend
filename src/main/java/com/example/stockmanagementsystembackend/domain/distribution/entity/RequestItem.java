package com.example.stockmanagementsystembackend.domain.distribution.entity;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "request_has_inventoryitem")
public class RequestItem {
    @EmbeddedId
    private RequestItemId id;

    @MapsId("requestOrdertid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Request_ordertID", nullable = false)
    private InternalRequest requestOrdertid;

    @MapsId("inventoryitemItemid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "InventoryItem_itemId", nullable = false)
    private InventoryItem inventoryitemItem;

}
