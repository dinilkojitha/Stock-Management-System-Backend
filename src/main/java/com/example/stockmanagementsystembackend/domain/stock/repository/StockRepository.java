package com.example.stockmanagementsystembackend.domain.stock.repository;

import com.example.stockmanagementsystembackend.domain.stock.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends JpaRepository<Stock, Integer> {

}
