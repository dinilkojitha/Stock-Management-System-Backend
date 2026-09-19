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
public class StockItemId implements Serializable {
    private static final long serialVersionUID = -6302149977593322743L;
    @NotNull
    @Column(name = "stock_id", nullable = false)
    private Integer stockId;

    @NotNull
    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        StockItemId entity = (StockItemId) o;
        return Objects.equals(this.itemId, entity.itemId) &&
                Objects.equals(this.stockId, entity.stockId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, stockId);
    }

}