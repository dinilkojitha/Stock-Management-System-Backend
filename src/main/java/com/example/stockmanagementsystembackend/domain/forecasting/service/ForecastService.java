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

    // Create Forecast method



    // Calculate Forecast method
    /* Calculates future demand using historical consumption.

       The calculation:
       1. Finds transactions belonging to the inventory item.
       2. Considers stock-out/consumption transactions
       3. Calculates average daily consumption
       4. Converts the daily consumption into the requested forecast period.

     */

    public double calculateForecast(
            Integer inventoryItemId,
            String forecastPeriod) {

        List<Transaction> transactions =
                transactionRepository.findAll();

        double totalConsumed = 0.0;

        int transactionCount = 0;

        for (Transaction transaction : transactions) {

            // Ignore transactions for other inventory items.
            if (transaction.getInventoryitemItem() == null ||
                    transaction.getInventoryitemItem().getId() == null) {

                continue;
            }

            if (!transaction.getInventoryitemItem()
                    .getId()
                    .equals(inventoryItemId)) {

                continue;
            }

            // Ignore transactions without a quantity.
            if (transaction.getQuantityChanged() == null) {

                continue;
            }

            /*
             * Negative quantity changes normally represent stock
             * being removed/issued.
             *
             * Convert the negative value into positive consumption.
             */
            double quantity =
                    transaction.getQuantityChanged();

            if (quantity < 0) {

                totalConsumed += Math.abs(quantity);

                transactionCount++;
            }

            /*
             * Some systems store stock-out quantities as positive.
             * Therefore also recognise transaction types that
             * indicate consumption/issuing.
             */
            else if (isConsumptionTransaction(transaction)) {

                totalConsumed += quantity;

                transactionCount++;
            }
        }

        /*
         * If there is no transaction history, we cannot make a
         * reliable historical demand forecast.
         *
         * Returning 0 prevents the system from inventing demand.
         */
        if (transactionCount == 0) {

            return 0.0;
        }

        /*
         * Average quantity consumed per transaction.
         */
        double averageConsumption =
                totalConsumed / transactionCount;

        /*
         * Convert the historical average into an estimated
         * demand for the selected planning period.
         *
         * This is a simple planning forecast, not an AI model.
         */
        double predictedDemand;

        if (forecastPeriod == null) {

            forecastPeriod = "Monthly";
        }

        switch (forecastPeriod.toLowerCase()) {

            case "daily":
            case "day":

                predictedDemand =
                        averageConsumption;

                break;

            case "weekly":
            case "week":

                predictedDemand =
                        averageConsumption * 7;

                break;

            case "monthly":
            case "month":

                predictedDemand =
                        averageConsumption * 30;

                break;

            case "quarterly":
            case "quarter":

                predictedDemand =
                        averageConsumption * 90;

                break;

            default:

                // Default to monthly planning.
                predictedDemand =
                        averageConsumption * 30;
        }

        return round(predictedDemand);
    }

    // check whether transaction IsConsumption
    private boolean isConsumptionTransaction(
            Transaction transaction) {

        if (transaction.getTransactionType() == null) {

            return false;
        }

        String type =
                transaction.getTransactionType()
                        .trim()
                        .toLowerCase();

        return type.equals("stock out")
                || type.equals("stock-out")
                || type.equals("stockout")
                || type.equals("issue")
                || type.equals("issued")
                || type.equals("consumption")
                || type.equals("consume");
    }


    // round method necessary for calculating forecasts
    private double round(double value) {

        return Math.round(value * 100.0) / 100.0;
    }

}