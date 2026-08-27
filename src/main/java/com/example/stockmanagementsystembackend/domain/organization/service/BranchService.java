package com.example.stockmanagementsystembackend.domain.organization.service;

import com.example.stockmanagementsystembackend.domain.organization.repository.BranchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

//    public void deleteBranch(int id){
//        branchRepository.deleteById(id);
//        return ResponseEntity.ok();
//    }



}
