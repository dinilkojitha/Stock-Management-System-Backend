package com.example.stockmanagementsystembackend.domain.audit.repository;

import com.example.stockmanagementsystembackend.domain.audit.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

}
