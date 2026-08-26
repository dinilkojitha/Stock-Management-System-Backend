package com.example.stockmanagementsystembackend.domain.organization.service;

import com.example.stockmanagementsystembackend.domain.organization.dto.request.BranchRequest;
import com.example.stockmanagementsystembackend.domain.organization.dto.response.*;
import com.example.stockmanagementsystembackend.domain.organization.entity.Branch;
import com.example.stockmanagementsystembackend.domain.organization.repository.DepartmentRepository;
import com.example.stockmanagementsystembackend.domain.stock.repository.StockRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BranchService {

    private final com.example.stockmanagementsystembackend.domain.organization.repository.BranchRepository branchRepository;
    private final DepartmentRepository departmentRepository;
    private final StockRepository stockRepository;

    public BranchService(com.example.stockmanagementsystembackend.domain.organization.repository.BranchRepository branchRepository,
                         DepartmentRepository departmentRepository, StockRepository stockRepository) {
        this.branchRepository = branchRepository;
        this.departmentRepository = departmentRepository;
        this.stockRepository = stockRepository;
    }

    public BranchResponse createBranch(BranchRequest request) {
        ensureUnique(request.getBranchName(), null);
        Branch branch = new Branch(); apply(branch, request);
        return response(branchRepository.save(branch));
    }

    @Transactional(readOnly = true)
    public List<BranchResponse> getAllBranches() { return branchRepository.findAll().stream().map(this::response).toList(); }

    @Transactional(readOnly = true)
    public BranchResponse getBranchById(Integer id) { return response(find(id)); }

    public BranchResponse updateBranch(Integer id, BranchRequest request) {
        Branch branch = find(id); ensureUnique(request.getBranchName(), id); apply(branch, request);
        return response(branchRepository.save(branch));
    }

    public void deleteBranch(Integer id) {
        Branch branch = find(id);
        if (departmentRepository.existsByBranchBranchid(branch)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Branch has departments and cannot be deleted");
        branchRepository.delete(branch);
    }

    @Transactional(readOnly = true)
    public BranchSummaryResponse getBranchStockSummary(Integer id) { return summary(find(id)); }

    @Transactional(readOnly = true)
    public BranchOverviewResponse getAllBranchesOverview() {
        List<BranchSummaryResponse> branches = branchRepository.findAll().stream().map(this::summary).toList();
        return new BranchOverviewResponse(branches, branches.size());
    }

    private Branch find(Integer id) { return branchRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found: " + id)); }
    private void ensureUnique(String name, Integer id) { branchRepository.findByBranchName(name).ifPresent(existing -> { if (!existing.getId().equals(id)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Branch name already exists"); }); }
    private void apply(Branch branch, BranchRequest request) { branch.setBranchName(request.getBranchName().trim()); branch.setLocation(request.getLocation()); }
    private BranchResponse response(Branch branch) { return new BranchResponse(branch.getId(), branch.getBranchName(), branch.getLocation()); }
    private BranchSummaryResponse summary(Branch branch) { return new BranchSummaryResponse(branch.getId(), branch.getBranchName(), branch.getLocation(), stockRepository.findByBranchBranchid(branch).size(), departmentRepository.findByBranchBranchid(branch).size()); }

//    public void deleteBranch(int id){
//        branchRepository.deleteById(id);
//        return ResponseEntity.ok();
//    }



}
