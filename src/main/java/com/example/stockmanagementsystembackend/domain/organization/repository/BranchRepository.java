package com.example.stockmanagementsystembackend.domain.organization.repository;

import com.example.stockmanagementsystembackend.domain.organization.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {
	Optional<Branch> findByBranchName(String branchName);
}
