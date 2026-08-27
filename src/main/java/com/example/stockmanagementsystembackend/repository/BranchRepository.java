package com.example.stockmanagementsystembackend.repository;

import com.example.stockmanagementsystembackend.entity.Branch;
import com.example.stockmanagementsystembackend.entity.Inventoryitem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch,Integer> {

}
