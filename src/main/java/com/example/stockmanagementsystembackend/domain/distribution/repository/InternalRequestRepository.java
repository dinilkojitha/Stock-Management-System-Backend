package com.example.stockmanagementsystembackend.domain.distribution.repository;

import com.example.stockmanagementsystembackend.domain.distribution.entity.InternalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InternalRequestRepository extends JpaRepository<InternalRequest, Integer> {

}
