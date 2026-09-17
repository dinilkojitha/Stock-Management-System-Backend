package com.example.stockmanagementsystembackend.domain.stock.repository;

import com.example.stockmanagementsystembackend.domain.stock.entity.Branchtransferrequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// TODO: Replace with a proper TransferResponse entity when implemented
@Repository
public interface TransferResponseRepository extends JpaRepository<Branchtransferrequest, Integer> {

}
