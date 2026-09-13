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
        return ResponseEntity.ok("gfdgg");
    }

}