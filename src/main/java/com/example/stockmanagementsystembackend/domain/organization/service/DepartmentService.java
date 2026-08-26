package com.example.stockmanagementsystembackend.domain.organization.service;

import com.example.stockmanagementsystembackend.domain.organization.dto.request.DepartmentRequest;
import com.example.stockmanagementsystembackend.domain.organization.dto.response.DepartmentResponse;
import com.example.stockmanagementsystembackend.domain.organization.entity.*;
import com.example.stockmanagementsystembackend.domain.organization.repository.*;
import com.example.stockmanagementsystembackend.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentService {
	private final DepartmentRepository departmentRepository;
	private final BranchRepository branchRepository;
	private final UserRepository userRepository;

	public DepartmentService(DepartmentRepository departmentRepository, BranchRepository branchRepository, UserRepository userRepository) {
		this.departmentRepository = departmentRepository; this.branchRepository = branchRepository; this.userRepository = userRepository;
	}
	public DepartmentResponse createDepartment(DepartmentRequest request) { Department d = new Department(); apply(d, request); return response(departmentRepository.save(d)); }
	@Transactional(readOnly = true) public List<DepartmentResponse> getAllDepartments() { return departmentRepository.findAll().stream().map(this::response).toList(); }
	@Transactional(readOnly = true) public DepartmentResponse getDepartmentById(Integer id) { return response(find(id)); }
	@Transactional(readOnly = true) public List<DepartmentResponse> getDepartmentsByBranch(Integer id) { return departmentRepository.findByBranchBranchid(branch(id)).stream().map(this::response).toList(); }
	public DepartmentResponse updateDepartment(Integer id, DepartmentRequest request) { Department d = find(id); apply(d, request); return response(departmentRepository.save(d)); }
	public void deleteDepartment(Integer id) { Department d = find(id); if (userRepository.countByDepartmentDepartment(d) > 0) throw new ResponseStatusException(HttpStatus.CONFLICT, "Department has users and cannot be deleted"); departmentRepository.delete(d); }
	private void apply(Department d, DepartmentRequest r) { d.setDepartmentName(r.getDepartmentName().trim()); d.setLocation(r.getLocation()); d.setBranchBranchid(branch(r.getBranchId())); }
	private Branch branch(Integer id) { return branchRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Branch not found: " + id)); }
	private Department find(Integer id) { return departmentRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Department not found: " + id)); }
	private DepartmentResponse response(Department d) { Branch b = d.getBranchBranchid(); return new DepartmentResponse(d.getId(), d.getDepartmentName(), d.getLocation(), b.getId(), b.getBranchName()); }

}
