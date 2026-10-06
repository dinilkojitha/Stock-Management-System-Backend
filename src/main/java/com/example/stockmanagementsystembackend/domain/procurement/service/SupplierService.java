package com.example.stockmanagementsystembackend.domain.procurement.service;

import com.example.stockmanagementsystembackend.Core.EmailService;
import com.example.stockmanagementsystembackend.domain.procurement.dto.SupplierRequest;
import com.example.stockmanagementsystembackend.domain.procurement.dto.SupplierResponse;
import com.example.stockmanagementsystembackend.domain.procurement.entity.Supplier;
import com.example.stockmanagementsystembackend.domain.procurement.repository.SupplierRepository;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

	private final SupplierRepository supplierRepository;


//	public SupplierService(EmailService emailService, SupplierRepository supplierRepository) {
//		this.emailService = emailService;
//		this.supplierRepository = supplierRepository;
//	}


	public SupplierService(SupplierRepository supplierRepository, EmailService emailService) {
		this.supplierRepository = supplierRepository;

    }

	public SupplierResponse create(SupplierRequest request) {
		Supplier supplier = new Supplier();
		apply(supplier, request);
		return response(supplierRepository.save(supplier));
	}

	@Transactional(readOnly = true)
	public List<SupplierResponse> findAll() {
		return supplierRepository.findAll().stream().map(this::response).toList();
	}

	@Transactional(readOnly = true)
	public SupplierResponse findById(Integer id) {
		return response(find(id));
	}

	public SupplierResponse update(Integer id, SupplierRequest request) {
		Supplier supplier = find(id);
		apply(supplier, request);
		return response(supplierRepository.save(supplier));
	}

	public void delete(Integer id) {
		supplierRepository.delete(find(id));
	}

	private Supplier find(Integer id) {
		return supplierRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found: " + id));
	}

	private void apply(Supplier supplier, SupplierRequest request) {
		supplier.setCompanyName(request.getCompanyName().trim());
		supplier.setContactPerson(request.getContactPerson());
		supplier.setEmail(request.getEmail());
		supplier.setPhoneNumber(request.getPhoneNumber());
		supplier.setAddress(request.getAddress());
	}

	private SupplierResponse response(Supplier supplier) {
		return new SupplierResponse(supplier.getId(), supplier.getCompanyName(), supplier.getContactPerson(),
				supplier.getEmail(), supplier.getPhoneNumber(), supplier.getAddress());
	}





}
