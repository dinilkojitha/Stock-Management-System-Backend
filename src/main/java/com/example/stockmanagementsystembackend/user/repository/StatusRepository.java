package com.example.stockmanagementsystembackend.user.repository;

import com.example.stockmanagementsystembackend.user.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
	Optional<Status> findByNameIgnoreCase(String name);
}
