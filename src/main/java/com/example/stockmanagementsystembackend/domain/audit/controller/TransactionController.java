package com.example.stockmanagementsystembackend.domain.audit.controller;

import com.example.stockmanagementsystembackend.domain.audit.entity.Transaction;
import com.example.stockmanagementsystembackend.domain.audit.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    TransactionService transactionService;

    public TransactionController(
            TransactionService transactionService
    ) {
        this.transactionService = transactionService;
    }

    // CREATE
    @PostMapping("/add-new")
    public ResponseEntity<Transaction> add(
            @RequestBody Transaction transaction
    ) {

        Transaction savedTransaction =
                transactionService.addTransaction(transaction);

        return new ResponseEntity<>(
                savedTransaction,
                HttpStatus.CREATED
        );
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<Transaction>> getAll() {

        return ResponseEntity.ok(
                transactionService.getAllTransactions()
        );
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getById(
            @PathVariable Integer id
    ) {

        return ResponseEntity.ok(
                transactionService.getTransactionById(id)
        );
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> update(
            @PathVariable Integer id,
            @RequestBody Transaction transaction
    ) {

        return ResponseEntity.ok(
                transactionService.updateTransaction(
                        id,
                        transaction
                )
        );
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Integer id
    ) {

        transactionService.deleteTransaction(id);

        return ResponseEntity.noContent().build();
    }





}