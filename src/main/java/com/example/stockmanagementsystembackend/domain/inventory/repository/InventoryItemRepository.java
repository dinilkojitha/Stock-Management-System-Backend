package com.example.stockmanagementsystembackend.domain.inventory.repository;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, Integer> {

}
