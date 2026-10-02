package com.example.stockmanagementsystembackend.domain.procurement.repository;

import com.example.stockmanagementsystembackend.domain.procurement.entity.Supplier;
import com.example.stockmanagementsystembackend.domain.procurement.entity.SupplierEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierEvaluationRepository extends JpaRepository<SupplierEvaluation, Integer> {
    List<SupplierEvaluation> findBySupplierOrderByEvaluatedOnDesc(Supplier supplier);
}