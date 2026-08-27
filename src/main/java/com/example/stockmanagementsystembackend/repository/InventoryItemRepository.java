package com.example.stockmanagementsystembackend.repository;

import com.example.stockmanagementsystembackend.entity.Inventoryitem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryItemRepository extends JpaRepository<Inventoryitem,Integer> {

}
