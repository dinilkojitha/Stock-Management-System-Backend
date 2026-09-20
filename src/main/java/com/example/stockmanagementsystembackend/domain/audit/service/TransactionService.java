package com.example.stockmanagementsystembackend.domain.audit.service;

import com.example.stockmanagementsystembackend.domain.audit.entity.Transaction;
import com.example.stockmanagementsystembackend.domain.audit.repository.TransactionRepository;
import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.repository.InventoryItemRepository;
import com.example.stockmanagementsystembackend.user.entity.User;
import com.example.stockmanagementsystembackend.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;


import java.time.Instant;
import java.util.List;

@Service
public class TransactionService {

    TransactionRepository transactionRepository;
    UserRepository userRepository;
    InventoryItemRepository inventoryItemRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            UserRepository userRepository,
            InventoryItemRepository inventoryItemRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }




}
