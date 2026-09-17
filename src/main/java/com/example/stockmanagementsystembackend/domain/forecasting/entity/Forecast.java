package com.example.stockmanagementsystembackend.domain.forecasting.entity;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "forecast")
public class Forecast {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "forecast_id", nullable = false)
    private Integer id;

    @Column(name = "predicted_demand")
    private Double predictedDemand;

    @Column(name = "forecast_date")
    private LocalDate forecastDate;

    @Size(max = 40)
    @Column(name = "forecast_period", length = 40)
    private String forecastPeriod;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_id", nullable = false)
    private InventoryItem item;

}