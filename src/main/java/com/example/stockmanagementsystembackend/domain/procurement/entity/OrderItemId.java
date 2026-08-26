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
public class OrderItemId implements Serializable {
    private static final long serialVersionUID = 9080052514203277690L;
    @NotNull
    @Column(name = "Order_orderID", nullable = false)
    private Integer orderOrderid;

    @NotNull
    @Column(name = "InventoryItem_itemId", nullable = false)
    private Integer inventoryitemItemid;

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        OrderItemId entity = (OrderItemId) o;
        return Objects.equals(this.orderOrderid, entity.orderOrderid) &&
                Objects.equals(this.inventoryitemItemid, entity.inventoryitemItemid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderOrderid, inventoryitemItemid);
    }

}
