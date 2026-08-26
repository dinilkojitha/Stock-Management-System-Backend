package com.example.stockmanagementsystembackend.domain.stock.controller;

import com.example.stockmanagementsystembackend.domain.stock.dto.request.StockTransferCreateRequest;
import com.example.stockmanagementsystembackend.domain.stock.service.StockTransferService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping(value = "/api/stock-transfers")
public class StockTransferController {
	private final StockTransferService transferService;
	private final com.example.stockmanagementsystembackend.domain.stock.service.StockTransferPdfService pdfService;
	public StockTransferController(StockTransferService transferService, com.example.stockmanagementsystembackend.domain.stock.service.StockTransferPdfService pdfService) { this.transferService = transferService; this.pdfService = pdfService; }
	@PostMapping public Object create(@Valid @RequestBody StockTransferCreateRequest request) { return transferService.createTransferRequest(request); }
	@GetMapping public Object getAll() { return transferService.getAllTransfers(); }
	@GetMapping("/{id}") public Object get(@PathVariable Integer id) { return transferService.getTransferById(id); }
	@GetMapping("/by-branch/{branchId}") public Object byBranch(@PathVariable Integer branchId) { return transferService.getTransfersByBranch(branchId); }
	@PutMapping("/{id}/approve") public Object approve(@PathVariable Integer id) { return transferService.approveTransfer(id); }
	@PutMapping("/{id}/reject") public Object reject(@PathVariable Integer id) { return transferService.rejectTransfer(id); }
	@GetMapping(value = "/{id}/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<byte[]> pdf(@PathVariable Integer id) {
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
				ContentDisposition.attachment().filename("Nexus-Inventory-Transfer-" + id + ".pdf").build().toString())
				.contentType(MediaType.APPLICATION_PDF).body(pdfService.createTransferPdf(id));
	}

}
