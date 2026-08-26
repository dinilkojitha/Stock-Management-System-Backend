package com.example.stockmanagementsystembackend.domain.procurement.repository;

import com.example.stockmanagementsystembackend.domain.procurement.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Integer> {

}
