package com.example.stockmanagementsystembackend.domain.distribution.entity;

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
public class RequestItemId implements Serializable {
    private static final long serialVersionUID = -6636377673522017483L;
    @NotNull
    @Column(name = "Request_ordertID", nullable = false)
    private Integer requestOrdertid;

    @NotNull
    @Column(name = "InventoryItem_itemId", nullable = false)
    private Integer inventoryitemItemid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RequestItemId entity = (RequestItemId) o;
        return Objects.equals(this.requestOrdertid, entity.requestOrdertid) &&
                Objects.equals(this.inventoryitemItemid, entity.inventoryitemItemid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestOrdertid, inventoryitemItemid);
    }

}
