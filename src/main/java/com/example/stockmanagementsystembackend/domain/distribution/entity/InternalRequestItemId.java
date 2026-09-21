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
public class InternalRequestItemId implements Serializable {
    private static final long serialVersionUID = -8926982748383201883L;
    @NotNull
    @Column(name = "request_id", nullable = false)
    private Integer requestId;

    @NotNull
    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        InternalRequestItemId entity = (InternalRequestItemId) o;
        return Objects.equals(this.itemId, entity.itemId) &&
                Objects.equals(this.requestId, entity.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, requestId);
    }

}