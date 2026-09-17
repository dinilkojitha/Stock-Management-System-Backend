package com.example.stockmanagementsystembackend.domain.procurement.entity;

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
public class PurchaseOrderItemId implements Serializable {
    private static final long serialVersionUID = 636339554422023885L;
    @NotNull
    @Column(name = "purchase_order_id", nullable = false)
    private Integer purchaseOrderId;

    @NotNull
    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        PurchaseOrderItemId entity = (PurchaseOrderItemId) o;
        return Objects.equals(this.itemId, entity.itemId) &&
                Objects.equals(this.purchaseOrderId, entity.purchaseOrderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, purchaseOrderId);
    }

}