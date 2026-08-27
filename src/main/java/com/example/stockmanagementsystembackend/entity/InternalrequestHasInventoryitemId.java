package com.example.stockmanagementsystembackend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class InternalrequestHasInventoryitemId implements Serializable {
    private static final long serialVersionUID = -4396363316600218611L;
    @NotNull
    @Column(name = "InternalRequest_ordertID", nullable = false)
    private Integer internalrequestOrdertid;

    @NotNull
    @Column(name = "InventoryItem_itemId", nullable = false)
    private Integer inventoryitemItemid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        InternalrequestHasInventoryitemId entity = (InternalrequestHasInventoryitemId) o;
        return Objects.equals(this.inventoryitemItemid, entity.inventoryitemItemid) &&
                Objects.equals(this.internalrequestOrdertid, entity.internalrequestOrdertid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventoryitemItemid, internalrequestOrdertid);
    }

}