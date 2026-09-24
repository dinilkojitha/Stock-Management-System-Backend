package com.example.stockmanagementsystembackend.domain.organization.controller;

import com.example.stockmanagementsystembackend.domain.organization.service.BranchService;
import com.example.stockmanagementsystembackend.domain.organization.dto.request.BranchRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/branches")
public class BranchController {

    private final BranchService branchService;

    public BranchController(BranchService branchService) {
        this.branchService = branchService;
    }

    @PostMapping("/create")         // work
    public Object create(@Valid @RequestBody BranchRequest request){
        return branchService.createBranch(request);
    }

    @GetMapping("/all")             // work
    public Object getAll() {
        return branchService.getAllBranches();
    }

    @GetMapping("/overview")             // work
    public Object overview() {
        return branchService.getAllBranchesOverview();
    }

    @GetMapping("/performance")             // work
    public Object performance() {
        return branchService.getAllBranchPerformance();
    }

    @GetMapping("/{id}")             // work
    public Object get(@PathVariable("id") Integer id) {
        return branchService.getBranchById(id);
    }

    @PutMapping("/{id}/update")             // work
    public Object update(@PathVariable("id") Integer id, @Valid @RequestBody BranchRequest request) {
        return branchService.updateBranch(id, request);
    }

    @DeleteMapping("/{id}/delete")             // work
    public void delete(@PathVariable("id") Integer id) {
        branchService.deleteBranch(id);
    }

    @GetMapping("/{id}/stock-summary")             // work
    public Object stockSummary(@PathVariable("id") Integer id) {
        return branchService.getBranchStockSummary(id);
    }

    @GetMapping("/{id}/inventory")             // work
    public Object inventory(@PathVariable("id") Integer id) {
        return branchService.getBranchInventory(id);
    }

    @GetMapping("/{id}/performance")             // work
    public Object branchPerformance(@PathVariable("id") Integer id) {
        return branchService.getBranchPerformance(id);
    }



}
