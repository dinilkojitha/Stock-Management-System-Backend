package com.example.stockmanagementsystembackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "request_has_inventoryitem")
public class RequestHasInventoryitem {
    @EmbeddedId
    private RequestHasInventoryitemId id;

    @MapsId("requestOrdertid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "Request_ordertID", nullable = false)
    private Internalrequest requestOrdertid;

    @MapsId("inventoryitemItemid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "InventoryItem_itemId", nullable = false)
    private Inventoryitem inventoryitemItem;

}