package com.example.stockmanagementsystembackend.domain.organization.repository;

import com.example.stockmanagementsystembackend.domain.organization.entity.Department;
import com.example.stockmanagementsystembackend.domain.organization.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
	List<Department> findByBranchBranchid(Branch branch);
	boolean existsByBranchBranchid(Branch branch);
}
