package com.example.stockmanagementsystembackend.domain.stock.entity;

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
public class StockTransferRequestItemId implements Serializable {
    private static final long serialVersionUID = 1362006617836037814L;
    @NotNull
    @Column(name = "BranchTransferRequeast_OrderID", nullable = false)
    private Integer branchtransferrequeastOrderid;

    @NotNull
    @Column(name = "InventoryItem_itemId", nullable = false)
    private Integer inventoryitemItemid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StockTransferRequestItemId entity = (StockTransferRequestItemId) o;
        return Objects.equals(this.inventoryitemItemid, entity.inventoryitemItemid) &&
                Objects.equals(this.branchtransferrequeastOrderid, entity.branchtransferrequeastOrderid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventoryitemItemid, branchtransferrequeastOrderid);
    }

}
