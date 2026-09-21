package com.example.stockmanagementsystembackend.domain.forecasting.controller;

import com.example.stockmanagementsystembackend.domain.forecasting.entity.Forecast;
import com.example.stockmanagementsystembackend.domain.forecasting.service.ForecastService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/forecasts")
public class ForecastController {

    private final ForecastService forecastService;

    public ForecastController(
            ForecastService forecastService) {

        this.forecastService = forecastService;
    }

    // CREATE FORECAST
    @PostMapping
    public ResponseEntity<String> createForecast(
            @RequestBody Forecast forecast) {

        return forecastService.createForecast(
                forecast
        );
    }

    // GET ALL FORECASTS
    @GetMapping
    public ResponseEntity<String> getAllForecasts() {

        return forecastService.getAllForecasts();
    }

    // GET FORECAST BY ID
    @GetMapping("/{id}")
    public ResponseEntity<String> getForecastById(
            @PathVariable Integer id) {

        return forecastService.getForecastById(
                id
        );
    }

    // UPDATE FORECAST
    @PutMapping("/{id}")
    public ResponseEntity<String> updateForecast(
            @PathVariable Integer id,
            @RequestBody Forecast forecast) {

        return forecastService.updateForecast(
                id,
                forecast
        );
    }

    // DELETE FORECAST
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteForecast(
            @PathVariable Integer id) {

        return forecastService.deleteForecast(
                id
        );
    }


    // CALCULATE FORECAST
    @GetMapping("/calculate/{inventoryItemId}")
    public ResponseEntity<String> calculateForecast(
            @PathVariable Integer inventoryItemId,
            @RequestParam(
                    defaultValue = "Monthly"
            )
            String forecastPeriod) {

        double predictedDemand =
                forecastService.calculateForecast(
                        inventoryItemId,
                        forecastPeriod
                );

        return ResponseEntity.ok(
                "Predicted demand for " +
                        forecastPeriod +
                        ": " +
                        predictedDemand
        );
    }

    // CALCULATE RECOMMENDED PURCHASE
    @GetMapping("/purchase/{inventoryItemId}")
    public ResponseEntity<String>
    calculateRecommendedPurchase(
            @PathVariable Integer inventoryItemId,
            @RequestParam(
                    defaultValue = "Monthly"
            )
            String forecastPeriod) {

        double recommendedPurchase =
                forecastService
                        .calculateRecommendedPurchase(
                                inventoryItemId,
                                forecastPeriod
                        );

        return ResponseEntity.ok(
                "Recommended purchase quantity for " +
                        forecastPeriod +
                        ": " +
                        recommendedPurchase
        );
    }

}