package com.example.stockmanagementsystembackend.domain.stock.repository;

import com.example.stockmanagementsystembackend.domain.stock.entity.StockTransferRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransferRequest, Integer> {

}
