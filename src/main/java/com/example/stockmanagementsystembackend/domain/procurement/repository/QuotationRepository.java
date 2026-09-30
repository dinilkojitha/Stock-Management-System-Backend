package com.example.stockmanagementsystembackend.domain.procurement.repository;

import com.example.stockmanagementsystembackend.domain.procurement.entity.Quotation;
import com.example.stockmanagementsystembackend.domain.procurement.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuotationRepository extends JpaRepository<Quotation, Integer> {
    List<Quotation> findByItemIdOrderByQuotedUnitCostAsc(Integer itemId);
    List<Quotation> findBySupplier(Supplier supplier);
}