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

    //CONSTRUCTOR
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

    // GET ALL TRANSACTIONS
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {

        return transactionRepository.findAll();
    }


    // GET TRANSACTION BY ID
    @Transactional(readOnly = true)
    public Transaction getTransactionById(Integer id) {

        validateId(id);

        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Transaction with ID " + id + " was not found"
                ));
    }

    // UPDATE TRANSACTION
    @Transactional
    public Transaction updateTransaction(
            Integer id,
            Transaction transaction
    ) {

        validateId(id);
        validateTransaction(transaction);

        Transaction existingTransaction =
                transactionRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Transaction with ID " + id +
                                        " was not found"
                        ));

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
                        "User with ID " + userId +
                                " was not found"
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

        InventoryItem item =
                inventoryItemRepository.findById(itemId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Inventory item with ID " + itemId +
                                        " was not found"
                        ));


        existingTransaction.setTransactionType(
                transaction.getTransactionType().trim()
        );

        existingTransaction.setUserUserid(user);

        existingTransaction.setItem(item);

        existingTransaction.setQuantityDelta(
                transaction.getQuantityDelta()
        );

        existingTransaction.setRemarks(
                transaction.getRemarks()
        );

        // Keep existing transaction time if no new time is provided
        if (transaction.getTransactedAt() != null) {

            existingTransaction.setTransactedAt(
                    transaction.getTransactedAt()
            );
        }

        return transactionRepository.save(existingTransaction);
    }

    // DELETE TRANSACTION
    @Transactional
    public void deleteTransaction(Integer id) {

        validateId(id);

        Transaction transaction =
                transactionRepository.findById(id)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Transaction with ID " + id +
                                        " was not found"
                        ));

        transactionRepository.delete(transaction);
    }

    // GET TRANSACTIONS BY USER
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByUser(
            Integer userId
    ) {

        validateId(userId);

        if (!userRepository.existsById(userId)) {

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User with ID " + userId +
                            " was not found"
            );
        }

        return transactionRepository
                .findByUserUseridId(userId);
    }

    // GET TRANSACTIONS BY TYPE
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByType(
            String transactionType
    ) {

        if (transactionType == null ||
                transactionType.trim().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Transaction type is required"
            );
        }

        return transactionRepository
                .findByTransactionTypeIgnoreCase(
                        transactionType.trim()
                );
    }








}
