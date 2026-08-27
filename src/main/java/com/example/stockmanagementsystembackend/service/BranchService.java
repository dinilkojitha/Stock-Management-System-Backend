package com.example.stockmanagementsystembackend.service;

import com.example.stockmanagementsystembackend.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

//    public void deleteBranch(int id){
//        branchRepository.deleteById(id);
//        return ResponseEntity.ok();
//    }



}
