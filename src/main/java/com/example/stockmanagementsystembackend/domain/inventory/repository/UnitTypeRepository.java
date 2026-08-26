package com.example.stockmanagementsystembackend.domain.inventory.repository;

import com.example.stockmanagementsystembackend.domain.inventory.entity.UnitType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitTypeRepository extends JpaRepository<UnitType, Integer> {

}
