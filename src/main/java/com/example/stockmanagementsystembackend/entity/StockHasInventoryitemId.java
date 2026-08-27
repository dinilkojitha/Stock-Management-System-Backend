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
public class StockHasInventoryitemId implements Serializable {
    private static final long serialVersionUID = 4452243352606810377L;
    @NotNull
    @Column(name = "Stock_batchID", nullable = false)
    private Integer stockBatchid;

    @NotNull
    @Column(name = "InventoryItem_itemId", nullable = false)
    private Integer inventoryitemItemid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StockHasInventoryitemId entity = (StockHasInventoryitemId) o;
        return Objects.equals(this.stockBatchid, entity.stockBatchid) &&
                Objects.equals(this.inventoryitemItemid, entity.inventoryitemItemid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stockBatchid, inventoryitemItemid);
    }

}