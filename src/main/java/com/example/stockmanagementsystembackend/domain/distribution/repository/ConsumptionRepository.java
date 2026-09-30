package com.example.stockmanagementsystembackend.domain.distribution.repository;

import com.example.stockmanagementsystembackend.domain.distribution.entity.Consumption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConsumptionRepository extends JpaRepository<Consumption, Integer> {

    Optional<Consumption> findByRequest_IdAndItem_Id(
            Integer requestId,
            Integer itemId
    );
}