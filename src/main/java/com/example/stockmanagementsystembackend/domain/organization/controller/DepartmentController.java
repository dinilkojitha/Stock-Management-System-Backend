package com.example.stockmanagementsystembackend.domain.organization.controller;

import com.example.stockmanagementsystembackend.domain.organization.dto.request.DepartmentRequest;
import com.example.stockmanagementsystembackend.domain.organization.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/departments")
public class DepartmentController {
	private final DepartmentService departmentService;

	public DepartmentController(DepartmentService departmentService) {
		this.departmentService = departmentService;
	}

	@PostMapping("/create")         // work
	public Object create(@Valid @RequestBody DepartmentRequest request) {
		return departmentService.createDepartment(request);
	}

	@GetMapping("/all")         // work
	public Object getAll() {
		return departmentService.getAllDepartments();
	}

	@GetMapping("/{id}")         // work
	public Object get(@PathVariable Integer id) {
		return departmentService.getDepartmentById(id);
	}

	@GetMapping("/by-branch/{branchId}")         // work
	public Object byBranch(@PathVariable Integer branchId) {
		return departmentService.getDepartmentsByBranch(branchId);
	}

	@PutMapping("/update/{id}")         // work
	public Object update(@PathVariable Integer id, @Valid @RequestBody DepartmentRequest request) {
		return departmentService.updateDepartment(id, request);
	}

	@DeleteMapping("/delete/{id}")         // work
	public void delete(@PathVariable Integer id) {
		departmentService.deleteDepartment(id);
	}

}
