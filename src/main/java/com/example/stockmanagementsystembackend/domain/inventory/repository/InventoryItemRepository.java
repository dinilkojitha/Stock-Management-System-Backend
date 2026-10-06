package com.example.stockmanagementsystembackend.domain.inventory.repository;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Integer> {
    List<InventoryItem> findByItemNameContainingIgnoreCaseOrderByItemNameAsc(String keyword);

    Optional<InventoryItem> findById(Integer id);
    List<InventoryItem> findByCategory_CategoryIdOrderByItemNameAsc(Integer categoryId);

    @Query("select i from InventoryItem i where i.totalQuantity <= i.reorderThreshold order by i.itemName")
    List<InventoryItem> findLowStock();

    // Serializes quantity adjustments and full updates without adding a version column.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select i from InventoryItem i where i.id = :id")
    Optional<InventoryItem> findByIdForUpdate(@Param("id") Integer id);
}
