package com.example.stockmanagementsystembackend.domain.forecasting.service;

import com.example.stockmanagementsystembackend.domain.audit.entity.Transaction;
import com.example.stockmanagementsystembackend.domain.audit.repository.TransactionRepository;
import com.example.stockmanagementsystembackend.domain.forecasting.entity.Forecast;
import com.example.stockmanagementsystembackend.domain.forecasting.repository.ForecastRepository;
import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.repository.InventoryItemRepository;
import com.example.stockmanagementsystembackend.user.entity.User;
import com.example.stockmanagementsystembackend.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class ForecastService {

    private final ForecastRepository forecastRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public ForecastService(
            ForecastRepository forecastRepository,
            InventoryItemRepository inventoryItemRepository,
            TransactionRepository transactionRepository,
            UserRepository userRepository) {

        this.forecastRepository = forecastRepository;
        this.inventoryItemRepository = inventoryItemRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    // CREATE FORECAST
    public ResponseEntity<String> createForecast(Forecast forecast) {

        if (forecast == null) {
            return ResponseEntity.badRequest()
                    .body("Forecast data is required");
        }

        if (forecast.getItem() == null ||
                forecast.getItem().getId() == null) {

            return ResponseEntity.badRequest()
                    .body("Inventory item is required");
        }

        Integer itemId = forecast.getItem().getId();

        InventoryItem inventoryItem =
                inventoryItemRepository
                        .findById(itemId)
                        .orElse(null);

        if (inventoryItem == null) {

            return ResponseEntity.badRequest()
                    .body("Inventory item not found");
        }

        String forecastPeriod =
                forecast.getForecastPeriod();

        if (forecastPeriod == null ||
                forecastPeriod.isBlank()) {

            forecastPeriod = "Monthly";
            forecast.setForecastPeriod(forecastPeriod);
        }

        double predictedDemand =
                calculateForecast(
                        itemId,
                        forecastPeriod
                );

        forecast.setItem(inventoryItem);
        forecast.setPredictedDemand(predictedDemand);

        if (forecast.getForecastDate() == null) {

            forecast.setForecastDate(
                    java.time.LocalDate.now()
            );
        }

        forecastRepository.save(forecast);

        return ResponseEntity.ok(
                "Forecast created successfully. " +
                        "Predicted demand: " +
                        predictedDemand
        );
    }


    // CALCULATE FORECAST
     /* Calculates future demand using historical consumption data
      * Historical period:
      * Previous 30 days.

      * Formula:
      * Total historical consumption
      * --------------------------------
      * Number of historical days = Average daily consumption
      *
      * Average daily consumption × Forecast period days = Predicted demand
      */

    public double calculateForecast(Integer inventoryItemId, String forecastPeriod) {

        InventoryItem inventoryItem =
                inventoryItemRepository
                        .findById(inventoryItemId)
                        .orElse(null);

        if (inventoryItem == null) {
            return 0.0;
        }

        // Look at the previous 30 days.
        int historicalDays = 30;

        Instant endDate = Instant.now();

        Instant startDate = endDate.minus(historicalDays, ChronoUnit.DAYS);

        List<Transaction> transactions = transactionRepository.findAll();

        double totalConsumption = 0.0;

        for (Transaction transaction : transactions) {

            // Ignore transactions without an item.
            if (transaction.getItem() == null) {
                continue;
            }

            // Only use transactions for the selected item.
            if (!transaction.getItem()
                    .getId()
                    .equals(inventoryItemId)) {
                continue;
            }

            // Ignore transactions without a date.
            if (transaction.getTransactedAt() == null) {
                continue;
            }

            // Only use transactions from the last 30 days.
            if (transaction.getTransactedAt()
                    .isBefore(startDate)
                    ||
                    transaction.getTransactedAt()
                            .isAfter(endDate)) {

                continue;
            }

            // Ignore transactions without quantity.
            if (transaction.getQuantityDelta() == null) {
                continue;
            }

            /*
             * Wastage is not normal consumption.
             * Therefore do not include it in demand.
             */
            if (isWastageTransaction(transaction)) {
                continue;
            }

            double quantity = transaction.getQuantityDelta();

            /*
             * If quantityDelta is negative,
             * stock was removed.
             *
             * Example:
             *
             * -10 kg tomatoes
             *
             * means 10 kg was consumed/issued.
             */
            if (quantity < 0) {
                totalConsumption += Math.abs(quantity);
            }

            /*
             * Some systems may store consumption
             * as a positive number and use the
             * transaction type to identify it.
             */
            else if (quantity > 0 &&
                    isConsumptionTransaction(
                            transaction)) {

                totalConsumption += quantity;
            }
        }

        /*
         * If there is no consumption history,
         * do not invent a demand value.
         */
        if (totalConsumption <= 0) {
            return 0.0;
        }

        // Calculate average daily consumption.
        double averageDailyConsumption = totalConsumption / historicalDays;

        // Determine the requested forecast period.
        int forecastDays = getForecastDays(forecastPeriod);

        // Calculate future demand.
        double predictedDemand = averageDailyConsumption * forecastDays;

        return round(predictedDemand);
    }

    // GET FORECAST PERIOD DAYS
    private int getForecastDays(String forecastPeriod) {

        if (forecastPeriod == null || forecastPeriod.isBlank()) {
            return 30;
        }

        switch (
                forecastPeriod.trim().toLowerCase()
        ) {

            case "daily":
            case "day":
                return 1;

            case "weekly":
            case "week":
                return 7;

            case "monthly":
            case "month":
                return 30;

            case "quarterly":
            case "quarter":
                return 90;

            default:
                return 30;
        }
    }

    // CHECK CONSUMPTION TRANSACTION
    private boolean isConsumptionTransaction(Transaction transaction) {

        if (transaction.getTransactionType() == null) {
            return false;
        }

        String type = transaction.getTransactionType().trim().toLowerCase();

        return type.equals("stock out")
                || type.equals("stock-out")
                || type.equals("stockout")
                || type.equals("issue")
                || type.equals("issued")
                || type.equals("consumption")
                || type.equals("consume");
    }

    // CHECK WASTAGE TRANSACTION
    private boolean isWastageTransaction(Transaction transaction) {

        if (transaction.getTransactionType() == null) {
            return false;
        }

        String type = transaction.getTransactionType().trim().toLowerCase();

        return type.equals("waste")
                || type.equals("wastage")
                || type.equals("expired")
                || type.equals("damaged")
                || type.equals("spoiled")
                || type.equals("spoilage");
    }

    // ROUND VALUES
    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

}