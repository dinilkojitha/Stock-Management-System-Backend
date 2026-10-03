package com.example.stockmanagementsystembackend.domain.inventory.repository;

import com.example.stockmanagementsystembackend.domain.inventory.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByNameContainingIgnoreCaseOrderByNameAsc(String keyword);
}
