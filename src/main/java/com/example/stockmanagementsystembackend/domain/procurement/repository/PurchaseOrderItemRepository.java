package com.example.stockmanagementsystembackend.domain.procurement.repository;

import com.example.stockmanagementsystembackend.domain.procurement.entity.PurchaseOrderItem;
import com.example.stockmanagementsystembackend.domain.procurement.entity.PurchaseOrderItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItem, PurchaseOrderItemId> {
    List<PurchaseOrderItem> findByPurchaseOrderId(Integer purchaseOrderId);
}