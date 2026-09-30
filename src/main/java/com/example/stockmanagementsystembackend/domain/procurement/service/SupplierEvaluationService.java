package com.example.stockmanagementsystembackend.domain.procurement.service;

import com.example.stockmanagementsystembackend.domain.procurement.dto.EvaluationRequest;
import com.example.stockmanagementsystembackend.domain.procurement.dto.SupplierRatingResponse;
import com.example.stockmanagementsystembackend.domain.procurement.entity.Supplier;
import com.example.stockmanagementsystembackend.domain.procurement.entity.SupplierEvaluation;
import com.example.stockmanagementsystembackend.domain.procurement.repository.SupplierEvaluationRepository;
import com.example.stockmanagementsystembackend.domain.procurement.repository.SupplierRepository;
import com.example.stockmanagementsystembackend.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class SupplierEvaluationService {
    private final SupplierEvaluationRepository evaluationRepository;
    private final SupplierRepository supplierRepository;
    private final UserRepository userRepository;

    public SupplierEvaluationService(SupplierEvaluationRepository evaluationRepository,
                                     SupplierRepository supplierRepository, UserRepository userRepository) {
        this.evaluationRepository = evaluationRepository;
        this.supplierRepository = supplierRepository;
        this.userRepository = userRepository;
    }

    public SupplierRatingResponse evaluate(Integer supplierId, EvaluationRequest request) {
        Supplier supplier = findSupplier(supplierId);
        SupplierEvaluation evaluation = new SupplierEvaluation();
        evaluation.setSupplier(supplier);
        evaluation.setEvaluatedBy(userRepository.findById(request.getEvaluatedByUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + request.getEvaluatedByUserId())));
        evaluation.setRating(request.getRating());
        evaluation.setDeliveryRating(request.getDeliveryRating());
        evaluation.setQualityRating(request.getQualityRating());
        evaluation.setComments(request.getComments());
        evaluation.setEvaluatedOn(LocalDate.now());
        evaluationRepository.save(evaluation);
        return rating(supplier);
    }

    @Transactional(readOnly = true)
    public SupplierRatingResponse rating(Integer supplierId) {
        return rating(findSupplier(supplierId));
    }

    private SupplierRatingResponse rating(Supplier supplier) {
        var evaluations = evaluationRepository.findBySupplierOrderByEvaluatedOnDesc(supplier);
        double average = evaluations.stream().mapToInt(SupplierEvaluation::getRating).average().orElse(0);
        return new SupplierRatingResponse(supplier.getId(), supplier.getCompanyName(), average, (long) evaluations.size());
    }

    private Supplier findSupplier(Integer id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found: " + id));
    }
}