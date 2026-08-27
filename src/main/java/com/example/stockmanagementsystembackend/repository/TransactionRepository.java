package com.example.stockmanagementsystembackend.repository;

import com.example.stockmanagementsystembackend.entity.Branch;
import com.example.stockmanagementsystembackend.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Integer> {

}
