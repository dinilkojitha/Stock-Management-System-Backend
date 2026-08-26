package com.example.stockmanagementsystembackend.domain.distribution.entity;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "internalrequest_has_inventoryitem")
public class InternalRequestItem {
    @EmbeddedId
    private InternalRequestItemId id;

    @MapsId("internalrequestOrdertid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "InternalRequest_ordertID", nullable = false)
    private InternalRequest internalrequestOrdertid;

    @MapsId("inventoryitemItemid")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "InventoryItem_itemId", nullable = false)
    private InventoryItem inventoryitemItem;

    @Column(name = "quantity")
    private Double quantity;

}
