package com.example.stockmanagementsystembackend.repository;

import com.example.stockmanagementsystembackend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository  extends JpaRepository<User,Integer> {

}
