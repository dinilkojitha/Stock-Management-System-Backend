package com.example.stockmanagementsystembackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "internalrequest_has_inventoryitem")
public class InternalrequestHasInventoryitem {
    @EmbeddedId
    private InternalrequestHasInventoryitemId id;

    @MapsId("internalrequestOrdertid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "InternalRequest_ordertID", nullable = false)
    private Internalrequest internalrequestOrdertid;

    @MapsId("inventoryitemItemid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "InventoryItem_itemId", nullable = false)
    private Inventoryitem inventoryitemItem;

    @Column(name = "quantity")
    private Double quantity;

}