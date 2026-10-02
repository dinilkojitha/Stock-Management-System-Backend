package com.example.stockmanagementsystembackend.domain.procurement.controller;

import com.example.stockmanagementsystembackend.domain.procurement.dto.SupplierRequest;
import com.example.stockmanagementsystembackend.domain.procurement.dto.EvaluationRequest;
import com.example.stockmanagementsystembackend.domain.procurement.service.SupplierService;
import com.example.stockmanagementsystembackend.domain.procurement.service.SupplierEvaluationService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/suppliers")
public class SupplierController {
	private final SupplierService supplierService;
	private final SupplierEvaluationService evaluationService;

	public SupplierController(SupplierService supplierService, SupplierEvaluationService evaluationService) {
		this.supplierService = supplierService;
		this.evaluationService = evaluationService;
	}

	@PostMapping
	public Object create(@Valid @RequestBody SupplierRequest request) {
		return supplierService.create(request);
	}

	@GetMapping
	public Object findAll() {
		return supplierService.findAll();
	}

	@GetMapping("/{id}")
	public Object findById(@PathVariable Integer id) {
		return supplierService.findById(id);
	}

	@PutMapping("/{id}")
	public Object update(@PathVariable Integer id, @Valid @RequestBody SupplierRequest request) {
		return supplierService.update(id, request);
	}

	@DeleteMapping("/{id}")
	public void delete(@PathVariable Integer id) {
		supplierService.delete(id);
	}

	@PostMapping("/{id}/evaluations")
	public Object evaluate(@PathVariable Integer id, @Valid @RequestBody EvaluationRequest request) {
		return evaluationService.evaluate(id, request);
	}

	@GetMapping("/{id}/rating")
	public Object rating(@PathVariable Integer id) {
		return evaluationService.rating(id);
	}

}
