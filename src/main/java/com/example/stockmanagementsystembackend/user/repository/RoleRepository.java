package com.example.stockmanagementsystembackend.user.repository;

import com.example.stockmanagementsystembackend.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

}
