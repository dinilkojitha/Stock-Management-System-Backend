package com.example.stockmanagementsystembackend.domain.audit.service;

import com.example.stockmanagementsystembackend.domain.audit.entity.Transaction;
import com.example.stockmanagementsystembackend.domain.audit.repository.TransactionRepository;
import com.example.stockmanagementsystembackend.domain.audit.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;


@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;

    public Transaction add (Transaction trans){
        return transactionRepository.save(trans);
    }
}

