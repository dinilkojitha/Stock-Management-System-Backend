package com.example.stockmanagementsystembackend.domain.organization.repository;

import com.example.stockmanagementsystembackend.domain.organization.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

}
