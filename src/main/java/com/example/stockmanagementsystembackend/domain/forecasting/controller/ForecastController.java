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

    public ForecastController(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    // Create Forecast
    @PostMapping
    public ResponseEntity<String> createForecast(
            @RequestBody Forecast forecast) {

        return forecastService.createForecast(forecast);
    }

    // Get all Forecasts
    @GetMapping
    public ResponseEntity<String> getAllForecasts() {

        return forecastService.getAllForecasts();
    }

    // Get one forecast
    @GetMapping("/{id}")
    public ResponseEntity<String> getForecastById(
            @PathVariable Integer id) {

        return forecastService.getForecastById(id);
    }



    // Calculate Forecast
    @GetMapping("/calculate/{inventoryItemId}")
    public ResponseEntity<String> calculateForecast(
            @PathVariable Integer inventoryItemId,
            @RequestParam(defaultValue = "Monthly")
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

}