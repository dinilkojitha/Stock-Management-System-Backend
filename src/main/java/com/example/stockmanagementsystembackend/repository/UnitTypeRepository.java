package com.example.stockmanagementsystembackend.repository;

import com.example.stockmanagementsystembackend.entity.Order;
import com.example.stockmanagementsystembackend.entity.Unittype;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UnitTypeRepository extends JpaRepository<Unittype,Integer> {

}
