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

    //constructor
    public TransactionService(
            TransactionRepository transactionRepository,
            UserRepository userRepository,
            InventoryItemRepository inventoryItemRepository
    ) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    // CREATE TRANSACTION
    public Transaction addTransaction(Transaction transaction) {

        // Validate transaction data
        validateTransaction(transaction);

        // Check user
        if (transaction.getUserUserid() == null ||
                transaction.getUserUserid().getId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User is required"
            );
        }

        Integer userId = transaction.getUserUserid().getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User with ID " + userId + " was not found"
                ));


        // Check inventory item
        if (transaction.getItem() == null ||
                transaction.getItem().getId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Inventory item is required"
            );
        }

        Integer itemId = transaction.getItem().getId();

        InventoryItem item = inventoryItemRepository.findById(itemId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Inventory item with ID " + itemId +
                                " was not found"
                ));


        // Set actual User and Item objects
        transaction.setUserUserid(user);
        transaction.setItem(item);


        // Automatically set transaction time
        if (transaction.getTransactedAt() == null) {
            transaction.setTransactedAt(Instant.now());
        }


        // Save transaction
        return transactionRepository.save(transaction);
    }

    private void validateTransaction(Transaction transaction) {

        if (transaction == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transaction data is required"
            );
        }

        if (transaction.getTransactionType() == null ||
                transaction.getTransactionType().trim().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transaction type is required"
            );
        }

        if (transaction.getTransactionType().trim().length() > 45) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transaction type must not exceed 45 characters"
            );
        }

        if (transaction.getQuantityDelta() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantity change is required"
            );
        }

        if (transaction.getQuantityDelta() == 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantity change cannot be zero"
            );
        }

        if (transaction.getRemarks() != null &&
                transaction.getRemarks().length() > 120) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Remarks must not exceed 120 characters"
            );
        }
    }




}
