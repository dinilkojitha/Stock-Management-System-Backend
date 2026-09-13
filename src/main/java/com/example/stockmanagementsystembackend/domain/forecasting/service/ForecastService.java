package com.example.stockmanagementsystembackend.domain.forecasting.service;

import com.example.stockmanagementsystembackend.domain.forecasting.entity.Forecast;
import com.example.stockmanagementsystembackend.domain.forecasting.repository.ForecastRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ForecastService {
    private final ForecastRepository forecastRepository;

    public ForecastService(ForecastRepository forecastRepository) {
        this.forecastRepository = forecastRepository;
    }

    // Create a forecast
    public ResponseEntity<String> createForecast(Forecast forecast) {
        forecastRepository.save(forecast);
        return ResponseEntity.ok("Forecast created successfully");
    }

    // Get all forecasts
    public ResponseEntity<String> getAllForecasts() {

        List<Forecast> forecasts =
                forecastRepository.findAll();

        if (forecasts.isEmpty()) {
            return ResponseEntity.ok(
                    "No forecasts found"
            );
        }

        StringBuilder result = new StringBuilder();

        for (Forecast forecast : forecasts) {

            result.append("Forecast ID: ")
                    .append(forecast.getId())
                    .append(", Predicted Demand: ")
                    .append(forecast.getPredictedDemand())
                    .append(", Forecast Date: ")
                    .append(forecast.getForecastDate())
                    .append(", Forecast Period: ")
                    .append(forecast.getForecastPeriod())
                    .append("\n");
        }

        return ResponseEntity.ok(result.toString());
    }

}