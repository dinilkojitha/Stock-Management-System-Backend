package com.example.stockmanagementsystembackend.domain.procurement.service;

import com.example.stockmanagementsystembackend.domain.inventory.entity.InventoryItem;
import com.example.stockmanagementsystembackend.domain.inventory.repository.InventoryItemRepository;
import com.example.stockmanagementsystembackend.domain.procurement.dto.QuotationRequest;
import com.example.stockmanagementsystembackend.domain.procurement.dto.QuotationResponse;
import com.example.stockmanagementsystembackend.domain.procurement.entity.Quotation;
import com.example.stockmanagementsystembackend.domain.procurement.entity.Supplier;
import com.example.stockmanagementsystembackend.domain.procurement.repository.QuotationRepository;
import com.example.stockmanagementsystembackend.domain.procurement.repository.SupplierRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class QuotationService {
    private final QuotationRepository quotationRepository;
    private final SupplierRepository supplierRepository;
    private final InventoryItemRepository inventoryItemRepository;

    public QuotationService(QuotationRepository quotationRepository, SupplierRepository supplierRepository,
                            InventoryItemRepository inventoryItemRepository) {
        this.quotationRepository = quotationRepository;
        this.supplierRepository = supplierRepository;
        this.inventoryItemRepository = inventoryItemRepository;
    }

    public QuotationResponse create(QuotationRequest request) {
        Quotation quotation = new Quotation();
        quotation.setSupplier(findSupplier(request.getSupplierId()));
        quotation.setItem(findItem(request.getItemId()));
        quotation.setQuotedUnitCost(request.getQuotedUnitCost());
        quotation.setAvailableQuantity(request.getAvailableQuantity());
        quotation.setValidUntil(request.getValidUntil());
        quotation.setStatus(request.getStatus() == null ? "RECEIVED" : request.getStatus().trim().toUpperCase());
        quotation.setNotes(request.getNotes());
        quotation.setCreatedAt(Instant.now());
        return response(quotationRepository.save(quotation));
    }

    @Transactional(readOnly = true)
    public List<QuotationResponse> findAll(Integer itemId) {
        List<Quotation> quotations = itemId == null ? quotationRepository.findAll()
                : quotationRepository.findByItemIdOrderByQuotedUnitCostAsc(itemId);
        return quotations.stream().map(this::response).toList();
    }

    private Supplier findSupplier(Integer id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found: " + id));
    }

    private InventoryItem findItem(Integer id) {
        return inventoryItemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inventory item not found: " + id));
    }

    private QuotationResponse response(Quotation quotation) {
        return new QuotationResponse(quotation.getId(), quotation.getSupplier().getId(),
                quotation.getSupplier().getCompanyName(), quotation.getItem().getId(), quotation.getItem().getItemName(),
                quotation.getQuotedUnitCost(), quotation.getAvailableQuantity(), quotation.getValidUntil(),
                quotation.getStatus(), quotation.getNotes(), quotation.getCreatedAt());
    }
}