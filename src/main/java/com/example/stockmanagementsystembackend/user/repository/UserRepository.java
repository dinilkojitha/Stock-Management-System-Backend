package com.example.stockmanagementsystembackend.user.repository;

import com.example.stockmanagementsystembackend.user.entity.User;
import com.example.stockmanagementsystembackend.domain.organization.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
	long countByDepartmentDepartment(Department department);
}
