package com.example.stockmanagementsystembackend.repository;

import com.example.stockmanagementsystembackend.entity.Forecast;
import com.example.stockmanagementsystembackend.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast,Integer> {

}
