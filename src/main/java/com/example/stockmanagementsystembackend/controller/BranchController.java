package com.example.stockmanagementsystembackend.controller;

import com.example.stockmanagementsystembackend.service.BranchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/branch")
public class BranchController {

    @Autowired
    private BranchService branchService;

//    @DeleteMapping
//    public ResponseEntity<String> deleteBranch(@PathVariable int id){
//        return branchService.deleteBranch(id);
//    }


}
