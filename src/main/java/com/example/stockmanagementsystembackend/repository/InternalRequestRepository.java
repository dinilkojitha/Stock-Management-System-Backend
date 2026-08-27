package com.example.stockmanagementsystembackend.repository;

import com.example.stockmanagementsystembackend.controller.InternalRequestController;
import com.example.stockmanagementsystembackend.entity.Category;
import com.example.stockmanagementsystembackend.entity.Internalrequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternalRequestRepository extends JpaRepository<Internalrequest,Integer> {

}
