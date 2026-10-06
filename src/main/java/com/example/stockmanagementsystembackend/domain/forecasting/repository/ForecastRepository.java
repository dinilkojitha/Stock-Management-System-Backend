package com.example.stockmanagementsystembackend.domain.forecasting.repository;

import com.example.stockmanagementsystembackend.domain.forecasting.entity.Forecast;
import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast, Integer> {
    void deleteByItem(InventoryItem item);

    List<Forecast> findAllByItem(InventoryItem item);
}
