package com.example.stockmanagementsystembackend.domain.distribution.controller;

import com.example.stockmanagementsystembackend.domain.distribution.entity.Internalrequest;
import com.example.stockmanagementsystembackend.domain.distribution.service.InternalRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.stockmanagementsystembackend.domain.distribution.dto.CreateInternalRequestDTO;

import java.util.List;

@RestController
@RequestMapping("/api/internal-requests")
@CrossOrigin
public class InternalRequestController {

    private final InternalRequestService internalRequestService;

    public InternalRequestController(InternalRequestService internalRequestService) {
        this.internalRequestService = internalRequestService;
    }

    @GetMapping
    public ResponseEntity<List<Internalrequest>> getAllRequests() {
        return ResponseEntity.ok(internalRequestService.getAllRequest());
    }

    @PostMapping
    public ResponseEntity<Internalrequest> createRequest(
            @RequestBody Internalrequest request) {

        return ResponseEntity.ok(
                internalRequestService.createRequest(request)
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Internalrequest> updateRequest(
            @PathVariable("id") Integer id,
            @RequestBody Internalrequest request) {

        return ResponseEntity.ok(
                internalRequestService.updateRequest(id, request)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRequest(
            @PathVariable("id") Integer id) {

        internalRequestService.deleteRequest(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/complete")
    public ResponseEntity<Internalrequest> createCompleteRequest(
            @RequestBody CreateInternalRequestDTO request) {

        return ResponseEntity.ok(
                internalRequestService.createCompleteRequest(request)
        );
    }

}