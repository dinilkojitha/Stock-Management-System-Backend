package com.example.stockmanagementsystembackend.domain.forecasting.service;

import com.example.stockmanagementsystembackend.domain.audit.entity.Transaction;
import com.example.stockmanagementsystembackend.domain.audit.repository.TransactionRepository;
import com.example.stockmanagementsystembackend.domain.forecasting.entity.Forecast;
import com.example.stockmanagementsystembackend.domain.forecasting.repository.ForecastRepository;
import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.repository.InventoryItemRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class ForecastService {

    private final ForecastRepository forecastRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final TransactionRepository transactionRepository;

    public ForecastService(
            ForecastRepository forecastRepository,
            InventoryItemRepository inventoryItemRepository,
            TransactionRepository transactionRepository) {

        this.forecastRepository = forecastRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.transactionRepository = transactionRepository;
    }


}