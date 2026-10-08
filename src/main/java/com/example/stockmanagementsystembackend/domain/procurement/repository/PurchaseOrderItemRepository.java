package com.example.stockmanagementsystembackend.domain.procurement.repository;

import com.example.stockmanagementsystembackend.domain.procurement.entity.PurchaseOrderItem;
import com.example.stockmanagementsystembackend.domain.procurement.entity.PurchaseOrderItemId;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, PurchaseOrderItemId> {
    List<PurchaseOrderItem> findByPurchaseOrderId(Integer purchaseOrderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from PurchaseOrderItem i where i.purchaseOrder.id = :orderId")
    List<PurchaseOrderItem> findByPurchaseOrderIdForUpdate(@Param("orderId") Integer orderId);
}