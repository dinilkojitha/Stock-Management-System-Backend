package com.example.stockmanagementsystembackend.repository;

import com.example.stockmanagementsystembackend.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status,Integer> {

}
