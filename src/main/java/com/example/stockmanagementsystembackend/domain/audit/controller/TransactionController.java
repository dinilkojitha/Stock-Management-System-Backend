package com.example.stockmanagementsystembackend.domain.audit.controller;

import com.example.stockmanagementsystembackend.domain.audit.entity.Transaction;
import com.example.stockmanagementsystembackend.domain.audit.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@CrossOrigin
@RestController
@RequestMapping(value = "/api/transactions")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("add-new")
    public Transaction add (@PathVariable Transaction trans){
        return transactionService.add(trans);
    }
}