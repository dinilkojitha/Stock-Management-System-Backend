package com.example.stockmanagementsystembackend.repository;

import com.example.stockmanagementsystembackend.entity.Category;
import com.example.stockmanagementsystembackend.entity.Inventoryitem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category,Integer> {

}
