package com.example.stockmanagementsystembackend.entity;

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
    @Column(name = "forecastId", nullable = false)
    private Integer id;

    @Column(name = "predictedDemand")
    private Double predictedDemand;

    @Column(name = "forecastDate")
    private LocalDate forecastDate;

    @Size(max = 40)
    @Column(name = "forecastPeriod", length = 40)
    private String forecastPeriod;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "User_userID", nullable = false)
    private User userUserid;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "InventoryItem_itemId", nullable = false)
    private Inventoryitem inventoryitemItem;

}