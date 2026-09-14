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
public class BranchTransferItemId implements Serializable {
    private static final long serialVersionUID = -6877661383928676216L;
    @NotNull
    @Column(name = "transfer_id", nullable = false)
    private Integer transferId;

    @NotNull
    @Column(name = "item_id", nullable = false)
    private Integer itemId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        BranchTransferItemId entity = (BranchTransferItemId) o;
        return Objects.equals(this.itemId, entity.itemId) &&
                Objects.equals(this.transferId, entity.transferId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, transferId);
    }

}