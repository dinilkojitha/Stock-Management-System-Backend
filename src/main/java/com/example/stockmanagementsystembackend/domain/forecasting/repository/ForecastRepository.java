package com.example.stockmanagementsystembackend.domain.forecasting.repository;

import com.example.stockmanagementsystembackend.domain.forecasting.entity.Forecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast, Integer> {

}
