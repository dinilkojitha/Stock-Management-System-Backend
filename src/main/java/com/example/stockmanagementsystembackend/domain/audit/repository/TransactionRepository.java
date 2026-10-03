package com.example.stockmanagementsystembackend.domain.audit.repository;

import com.example.stockmanagementsystembackend.domain.audit.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    List<Transaction> findByUserUseridId(Integer userId);

    List<Transaction> findByItemId(Integer itemId);

    List<Transaction> findByTransactionTypeIgnoreCase(String transactionType);
}


