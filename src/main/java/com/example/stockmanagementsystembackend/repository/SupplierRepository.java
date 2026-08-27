package com.example.stockmanagementsystembackend.repository;

import com.example.stockmanagementsystembackend.entity.Inventoryitem;
import com.example.stockmanagementsystembackend.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier,Integer> {

}
