package com.example.stockmanagementsystembackend.domain.organization.controller;

import com.example.stockmanagementsystembackend.domain.organization.service.BranchService;
import com.example.stockmanagementsystembackend.domain.organization.dto.request.BranchRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/branches")
public class BranchController {

    @Autowired
    private BranchService branchService;

    @PostMapping
    public Object create(@Valid @RequestBody BranchRequest request) {
        return branchService.createBranch(request);
    }

    @GetMapping
    public Object getAll() {
        return branchService.getAllBranches();
    }

    @GetMapping("/overview")
    public Object overview() {
        return branchService.getAllBranchesOverview();
    }

    @GetMapping("/{id}")
    public Object get(@PathVariable Integer id) {
        return branchService.getBranchById(id);
    }

    @PutMapping("/{id}")
    public Object update(@PathVariable Integer id, @Valid @RequestBody BranchRequest request) {
        return branchService.updateBranch(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        branchService.deleteBranch(id);
    }

    @GetMapping("/{id}/stock-summary")
    public Object stockSummary(@PathVariable Integer id) {
        return branchService.getBranchStockSummary(id);
    }
    // @DeleteMapping
    // public ResponseEntity<String> deleteBranch(@PathVariable int id){
    // return branchService.deleteBranch(id);
    // }

}
