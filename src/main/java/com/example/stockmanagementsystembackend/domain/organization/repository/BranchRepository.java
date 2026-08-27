package com.example.stockmanagementsystembackend.domain.organization.repository;

import com.example.stockmanagementsystembackend.domain.organization.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {

}
