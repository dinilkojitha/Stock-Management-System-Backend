package com.example.stockmanagementsystembackend.domain.organization.controller;

import com.example.stockmanagementsystembackend.domain.organization.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/branches")
public class BranchController {

    @Autowired
    private BranchService branchService;

//    @DeleteMapping
//    public ResponseEntity<String> deleteBranch(@PathVariable int id){
//        return branchService.deleteBranch(id);
//    }


}
