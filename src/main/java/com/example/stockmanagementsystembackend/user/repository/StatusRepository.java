package com.example.stockmanagementsystembackend.user.repository;

import com.example.stockmanagementsystembackend.user.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {

}
