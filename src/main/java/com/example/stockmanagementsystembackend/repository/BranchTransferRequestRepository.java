package com.example.stockmanagementsystembackend.repository;

import com.example.stockmanagementsystembackend.entity.Branchtransferrequest;
import com.example.stockmanagementsystembackend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchTransferRequestRepository extends JpaRepository<Branchtransferrequest,Integer> {

}
