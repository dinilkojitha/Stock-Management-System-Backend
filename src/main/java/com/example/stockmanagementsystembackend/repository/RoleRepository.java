package com.example.stockmanagementsystembackend.repository;

import com.example.stockmanagementsystembackend.entity.Branch;
import com.example.stockmanagementsystembackend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role,Integer> {

}
